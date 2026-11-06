$ErrorActionPreference = 'Stop'
$repo = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
$out = Join-Path $repo 'csys-service/target/work-order-regression'
$cache = Join-Path $env:USERPROFILE '.m2/repository'
$junit = Join-Path $repo 'csys-service/target/textbook-regression/junit-platform-console-standalone-1.8.2.jar'
$h2 = Join-Path $repo 'csys-service/target/textbook-regression/h2-2.1.214.jar'
foreach ($dependency in @($junit, $h2)) {
    if (!(Test-Path -LiteralPath $dependency)) { throw "Missing cached test dependency: $dependency" }
}
$jars = @(Get-ChildItem -LiteralPath $cache -Recurse -Filter '*.jar' -File |
    Where-Object {
        $_.FullName -notmatch '\\org\\(apache\\maven|codehaus|sonatype|eclipse|apache\\xmlgraphics|bouncycastle|mongodb|lucene|tukaani|junit)|\\com\\hankcs|\\io\\netty|\\io\\prometheus|commons-lang3[\\/](3\.7|3\.14\.0)|commons-io[\\/]2\.6'
    } | Select-Object -ExpandProperty FullName)
$cp = (@(
    $out
    $junit
    $h2
    (Join-Path $cache 'org/mongodb/bson/4.6.1/bson-4.6.1.jar')
    (Join-Path (Split-Path $repo) 'curriculum-build-libs/*')
) + $jars) -join ';'
New-Item -ItemType Directory -Force -Path $out | Out-Null
$quote = { param($value) '"' + ($value -replace '\\', '/') + '"' }
$sources = @(
    'csys-service/src/main/java/com/doinner/csys/permission/CoursePermissionException.java'
    'csys-service/src/main/java/com/doinner/csys/permission/CoursePermissionExceptionHandler.java'
    'csys-service/src/main/java/com/doinner/csys/service/WorkOrderService.java'
    'csys-service/src/main/java/com/doinner/csys/controller/WorkOrderController.java'
    'csys-service/src/test/java/com/doinner/csys/WorkOrderServiceTest.java'
) | ForEach-Object { & $quote (Join-Path $repo $_) }
$compile = @('--release', '11', '-encoding', 'UTF-8', '-cp', (& $quote $cp), '-d', (& $quote $out)) + $sources
$compileFile = Join-Path $out 'compile.args'
[System.IO.File]::WriteAllLines($compileFile, $compile, (New-Object System.Text.UTF8Encoding($false)))
& javac "@$compileFile"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
$run = @('-cp', (& $quote $cp), 'org.junit.platform.console.ConsoleLauncher',
    '--select-class', 'com.doinner.csys.WorkOrderServiceTest', '--disable-ansi-colors', '--details=summary')
$runFile = Join-Path $out 'run.args'
[System.IO.File]::WriteAllLines($runFile, $run, (New-Object System.Text.UTF8Encoding($false)))
& java "@$runFile"
exit $LASTEXITCODE
