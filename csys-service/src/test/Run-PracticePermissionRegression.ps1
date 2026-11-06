$ErrorActionPreference = 'Stop'
$repo = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
$out = Join-Path $repo 'csys-service/target/practice-permission-regression'
$cache = Join-Path $env:USERPROFILE '.m2/repository'
$junit = Join-Path $repo 'csys-service/target/textbook-regression/junit-platform-console-standalone-1.8.2.jar'
$jars = @(Get-ChildItem -LiteralPath $cache -Recurse -Filter '*.jar' -File |
    Where-Object { $_.FullName -notmatch '\\org\\(apache\\maven|codehaus|sonatype|eclipse|apache\\xmlgraphics|bouncycastle|mongodb|lucene|tukaani|junit)|\\com\\hankcs|\\io\\netty|\\io\\prometheus|commons-lang3[\\/](3\.7|3\.14\.0)|commons-io[\\/]2\.6' } |
    Select-Object -ExpandProperty FullName)
$cp = (@($out, (Join-Path $repo 'csys-service/target/course-permission-regression'), (Join-Path $repo 'csys-service/target/assessment-regression'), $junit, (Join-Path $cache 'org/mongodb/bson/4.6.1/bson-4.6.1.jar'), (Join-Path (Split-Path $repo) 'curriculum-build-libs/*')) + $jars) -join ';'
New-Item -ItemType Directory -Force -Path $out | Out-Null
$quote = { param($value) '"' + ($value -replace '\\', '/') + '"' }
$sources = @(
    'csys-service-api/src/main/java/com/doinner/csys/domain/Course.java'
    'csys-service-api/src/main/java/com/doinner/csys/domain/vo/TeachingPlanListVo.java'
    'csys-service-api/src/main/java/com/doinner/csys/domain/vo/CoursePermissionVo.java'
    'csys-service/src/main/java/com/doinner/csys/permission/CoursePermissionException.java'
    'csys-service/src/main/java/com/doinner/csys/permission/CourseResource.java'
    'csys-service/src/main/java/com/doinner/csys/permission/CoursePermission.java'
    'csys-service/src/main/java/com/doinner/csys/permission/CoursePermissionAspect.java'
    'csys-service/src/main/java/com/doinner/csys/permission/CoursePermissionService.java'
    'csys-service/src/test/java/com/doinner/csys/CoursePermissionIdentityTest.java'
) | ForEach-Object { & $quote (Join-Path $repo $_) }
$compile = @('--release', '11', '-encoding', 'UTF-8', '-cp', (& $quote $cp), '-d', (& $quote $out)) + $sources
$compileFile = Join-Path $out 'compile.args'
[System.IO.File]::WriteAllLines($compileFile, $compile, (New-Object System.Text.UTF8Encoding($false)))
& javac "@$compileFile"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
$run = @('-cp', (& $quote $cp), 'org.junit.platform.console.ConsoleLauncher', '--select-class', 'com.doinner.csys.CoursePermissionIdentityTest', '--disable-ansi-colors', '--details=summary')
$runFile = Join-Path $out 'run.args'
[System.IO.File]::WriteAllLines($runFile, $run, (New-Object System.Text.UTF8Encoding($false)))
& java "@$runFile"
exit $LASTEXITCODE
