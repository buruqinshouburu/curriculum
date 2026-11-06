param([switch]$IncludeWordRegression)

$ErrorActionPreference = 'Stop'
$repo = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
$output = Join-Path $repo 'csys-service/target/target-design-content-regression'
$cache = Join-Path $repo 'csys-service/target/textbook-regression'
$cachedClasspath = Join-Path $cache 'classpath.txt'
if (!(Test-Path -LiteralPath $cachedClasspath)) { throw "Missing cached classpath: $cachedClasspath" }
$dependencies = @((Get-Content -LiteralPath $cachedClasspath -Raw).Trim().Split(';') |
    Where-Object { $_ -notmatch '\\org\\junit\\|commons-io[\\/]2\.6|commons-lang3[\\/](3\.7|3\.14\.0)' })
$classpath = (@(
    $output
    (Join-Path $cache 'junit-platform-console-standalone-1.8.2.jar')
    (Join-Path $env:USERPROFILE '.m2/repository/commons-io/commons-io/2.15.1/commons-io-2.15.1.jar')
    (Join-Path $env:USERPROFILE '.m2/repository/org/apache/commons/commons-compress/1.25.0/commons-compress-1.25.0.jar')
    (Join-Path $repo 'csys-service/target/assessment-regression')
    (Join-Path $repo 'csys-service/target/backend-rule-regression')
    (Join-Path $repo 'csys-service/target/objective-regression')
    (Join-Path $cache 'api-check')
) + $dependencies) -join ';'
$sourcepath = @(
    (Join-Path $repo 'csys-service/src/main/java')
    (Join-Path $repo 'csys-service-api/src/main/java')
) -join ';'
$sources = @(
    'csys-service/src/main/java/com/doinner/csys/service/impl/TeachingPlanServiceImpl.java'
    'csys-service/src/main/java/com/doinner/csys/service/impl/TeachingPlanModuleServiceImpl.java'
    'csys-service/src/main/java/com/doinner/csys/entity/csys/TeachingPlanWordImporter.java'
    'csys-service/src/main/java/com/doinner/csys/entity/csys/CourseTeachingPlanGenerator.java'
    'csys-service-api/src/main/java/com/doinner/csys/domain/TeachingPlanContent.java'
    'csys-service-api/src/main/java/com/doinner/csys/domain/TeachingPlanTargetDesign.java'
    'csys-service-api/src/main/java/com/doinner/csys/domain/TeachingPlanTextbook.java'
    'csys-service/src/test/java/com/doinner/csys/TeachingPlanTargetDesignContentImportTest.java'
)
$tests = @('com.doinner.csys.TeachingPlanTargetDesignContentImportTest')
if ($IncludeWordRegression) {
    $sources += 'csys-service/src/test/java/com/doinner/csys/TeachingPlanTargetDesignWordTest.java'
    $tests += 'com.doinner.csys.TeachingPlanTargetDesignWordTest'
}
New-Item -ItemType Directory -Force -Path $output | Out-Null
$quote = { param($value) '"' + ($value -replace '\\', '/') + '"' }
$compileArgs = @('--release', '11', '-encoding', 'UTF-8', '-cp', (& $quote $classpath),
    '-sourcepath', (& $quote $sourcepath), '-d', (& $quote $output)) +
    @($sources | ForEach-Object { & $quote (Join-Path $repo $_) })
$compileFile = Join-Path $output 'compile.args'
[System.IO.File]::WriteAllLines($compileFile, $compileArgs, (New-Object System.Text.UTF8Encoding($false)))
& javac "@$compileFile"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
$runArgs = @('-cp', (& $quote $classpath), 'org.junit.platform.console.ConsoleLauncher',
    '--disable-ansi-colors', '--details=summary', '--fail-if-no-tests')
foreach ($test in $tests) { $runArgs += '--select-class', $test }
$logConfig = Join-Path $repo 'csys-service/target/backend-rule-regression/logback-tests.xml'
if (Test-Path -LiteralPath $logConfig) {
    $runArgs = @(& $quote "-Dlogback.configurationFile=$logConfig") + $runArgs
}
$runArgs = @(& $quote "-DteachingPlan.regression.output=$output") + $runArgs
$runFile = Join-Path $output 'run.args'
[System.IO.File]::WriteAllLines($runFile, $runArgs, (New-Object System.Text.UTF8Encoding($false)))
& java "@$runFile" *> (Join-Path $output 'results.txt')
$testExit = $LASTEXITCODE
Get-Content -LiteralPath (Join-Path $output 'results.txt') -Tail 150
exit $testExit
