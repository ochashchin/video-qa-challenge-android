param (
    [string]$ClientId = $env:XRAY_CLIENT_ID,
    [string]$ClientSecret = $env:XRAY_CLIENT_SECRET,
    [string]$ProjectKey = "VQA",
    [string]$TestPlanKey = $env:TEST_PLAN_KEY
)

if (-not $TestPlanKey) {
    $TestPlanKey = "VQA-74"
}

if (-not $ClientId -or -not $ClientSecret) {
    Write-Error "XRAY_CLIENT_ID or XRAY_CLIENT_SECRET is missing."
    exit 1
}

# 1. Authenticate with Xray Cloud
Write-Host "Authenticating with Xray Cloud API..."
$authBody = @{
    client_id     = $ClientId
    client_secret = $ClientSecret
} | ConvertTo-Json

$token = Invoke-RestMethod -Uri "https://xray.cloud.getxray.app/api/v2/authenticate" `
                           -Method POST `
                           -Body $authBody `
                           -ContentType "application/json"

$headersJson = @{ "Authorization" = "Bearer $token"; "Content-Type" = "application/json" }

# 2. Fetch Test Plan summary from Xray GraphQL
$planSummary = "Smoke test"
try {
    $q = 'query { getTestPlans(jql: "key = ''' + $TestPlanKey + '''", limit: 1) { results { jira(fields: ["summary"]) } } }'
    $planRes = Invoke-RestMethod -Uri "https://xray.cloud.getxray.app/api/v2/graphql" -Method POST -Headers $headersJson -Body (@{ query = $q } | ConvertTo-Json)
    if ($planRes.data.getTestPlans.results.Count -gt 0) {
        $planSummary = $planRes.data.getTestPlans.results[0].jira.summary
    }
} catch {
    Write-Host "Using default plan summary: $planSummary"
}

$execSummary = "Test Execution - $planSummary"
Write-Host "Setting Test Execution Name: $execSummary"

# 3. Find all XML result files
$reportFiles = Get-ChildItem -Path "app/build/outputs/androidTest-results/connected" -Filter "*.xml" -Recurse -ErrorAction SilentlyContinue

if (-not $reportFiles -or $reportFiles.Count -eq 0) {
    Write-Warning "No test report XML files found under app/build/outputs/androidTest-results/connected."
    exit 0
}

foreach ($file in $reportFiles) {
    Write-Host "Processing $($file.FullName)..."

    $xmlContent = [System.IO.File]::ReadAllText($file.FullName)

    # Inject test_key property so Xray matches existing Jira tests (VQA-XX) without creating duplicates
    $xmlContent = [regex]::Replace($xmlContent, '<testcase name="verify_(\d+)"([^>]*)/>', '<testcase name="verify_$1"$2><properties><property name="test_key" value="VQA-$1"/></properties></testcase>')
    $xmlContent = [regex]::Replace($xmlContent, '<testcase name="verify_(\d+)"([^>]*)>(?!<properties>)', '<testcase name="verify_$1"$2><properties><property name="test_key" value="VQA-$1"/></properties>')

    # Prepare multipart form payload
    $infoJson = @{
        fields = @{
            summary   = $execSummary
            project   = @{ key = $ProjectKey }
            issuetype = @{ name = "Test Execution" }
        }
        xrayFields = @{
            testPlanKey = $TestPlanKey
        }
    } | ConvertTo-Json

    $boundary = [System.Guid]::NewGuid().ToString()
    $LF = "`r`n"
    $bodyBytes = [System.Collections.Generic.List[byte]]::new()

    $partInfo = "--$boundary$LF" +
                "Content-Disposition: form-data; name=`"info`"; filename=`"info.json`"$LF" +
                "Content-Type: application/json$LF$LF" +
                $infoJson + "$LF"
    $bodyBytes.AddRange([System.Text.Encoding]::UTF8.GetBytes($partInfo))

    $partResult = "--$boundary$LF" +
                  "Content-Disposition: form-data; name=`"results`"; filename=`"report.xml`"$LF" +
                  "Content-Type: application/xml$LF$LF" +
                  $xmlContent + "$LF" +
                  "--$boundary--$LF"
    $bodyBytes.AddRange([System.Text.Encoding]::UTF8.GetBytes($partResult))

    $uploadUrl = "https://xray.cloud.getxray.app/api/v2/import/execution/junit/multipart"
    $headersMultipart = @{
        "Authorization" = "Bearer $token"
        "Content-Type"  = "multipart/form-data; boundary=$boundary"
    }

    try {
        $response = Invoke-RestMethod -Uri $uploadUrl `
                                      -Method POST `
                                      -Headers $headersMultipart `
                                      -Body $bodyBytes.ToArray()
        Write-Host "=========================================================="
        Write-Host "Successfully imported to Xray!"
        Write-Host "Test Execution Key: $($response.key)"
        Write-Host "View in Jira: https://fiverrtesttracking.atlassian.net/browse/$($response.key)"
        Write-Host "=========================================================="
    } catch {
        Write-Host "Failed to upload $($file.Name): $($_.Exception.Message)"
        if ($_.ErrorDetails) {
            Write-Host "Detail: $($_.ErrorDetails.Message)"
        }
    }
}