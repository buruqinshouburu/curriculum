$ErrorActionPreference = 'Stop'
$root = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
$output = Join-Path $root 'csys-service/target/objective-regression'
$neighbor = Join-Path $root '../curriculum-latest'
$cache = Join-Path $HOME '.m2/repository'

# Local fallback only: compile current sources against already cached dependencies.
$jars = @(Get-ChildItem -LiteralPath $cache -Recurse -Filter '*.jar' -File |
    Where-Object {
        $_.FullName -notmatch '\\org\\(apache\\maven|codehaus|sonatype|eclipse|apache\\xmlgraphics|bouncycastle|mongodb|lucene|tukaani)|\\com\\hankcs|\\io\\netty|\\io\\prometheus|commons-lang3[\\/](3\.7|3\.14\.0)|commons-io[\\/]2\.6'
    } | Select-Object -ExpandProperty FullName)
$classpath = (@(
    $output
    (Join-Path $cache 'commons-io/commons-io/2.15.1/commons-io-2.15.1.jar')
    (Join-Path $cache 'org/apache/commons/commons-compress/1.25.0/commons-compress-1.25.0.jar')
    (Join-Path $cache 'org/mongodb/bson/4.6.1/bson-4.6.1.jar')
    (Join-Path $neighbor 'csys-service/target/classes')
    (Join-Path $neighbor 'csys-service-api/target/classes')
) + $jars) -join ';'
$sourcepath = @(
    (Join-Path $root 'csys-service/src/main/java')
    (Join-Path $root 'csys-service-api/src/main/java')
) -join ';'
$sources = @(
    (Join-Path $root 'csys-service/src/main/java/com/doinner/csys/service/impl/TeachingPlanModuleServiceImpl.java')
    (Join-Path $root 'csys-service/src/main/java/com/doinner/csys/service/impl/TeachingPlanServiceImpl.java')
    (Join-Path $root 'csys-service/src/main/java/com/doinner/csys/entity/csys/CourseTeachingPlanGenerator.java')
    (Join-Path $root 'csys-service/src/main/java/com/doinner/csys/entity/csys/TeachingPlanWordImporter.java')
    (Join-Path $root 'csys-service/src/test/java/com/doinner/csys/TeachingPlanObjectiveBatchSaveTest.java')
    (Join-Path $root 'csys-service/src/test/java/com/doinner/csys/TeachingPlanObjectiveWordTest.java')
    (Join-Path $root 'csys-service/src/test/java/com/doinner/csys/TeachingPlanTargetDesignWordTest.java')
)
New-Item -ItemType Directory -Force -Path $output | Out-Null
$report = Join-Path $output 'test-results.txt'
Write-Output 'Offline objective regression tests (no Spring bootstrap or database)' |
    Tee-Object -FilePath $report
& javac --release 11 -encoding UTF-8 -cp $classpath -sourcepath $sourcepath -d $output @sources
if ($LASTEXITCODE -ne 0) { throw 'Regression compilation failed' }
foreach ($test in @('TeachingPlanObjectiveWordTest', 'TeachingPlanObjectiveBatchSaveTest', 'TeachingPlanTargetDesignWordTest')) {
    & java "-DteachingPlan.regression.output=$output" -cp $classpath "com.doinner.csys.$test" 2>&1 | Tee-Object -FilePath $report -Append
    if ($LASTEXITCODE -ne 0) { throw "$test failed" }
}
Write-Output "Report: $report"
