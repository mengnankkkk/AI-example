# PowerShell script to fix test configuration issues

$testFile = "src\test\java\edu\qlu\chatbot\service\VoiceprintServiceTest.java"

# Add config.getGroupId() mock to tests that need it
$content = Get-Content $testFile -Raw

# Replace patterns to add the missing config mock
$content = $content -replace '(@Test\s+void\s+test\w+.*?)(\s+when\(userMapper)', '$1when(config.getGroupId()).thenReturn("test_group_123");$2'

# Replace patterns to add the missing config mock for identification tests
$content = $content -replace '(@Test\s+void\s+testIdentify.*?)(\s+when\(audioProcessingService)', '$1when(config.getGroupId()).thenReturn("test_group_123");$2'

Set-Content $testFile $content
Write-Host "Test file updated successfully"
