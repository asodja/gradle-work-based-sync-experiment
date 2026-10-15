# M3 comparison: builder output vs task bundles on gradle/gradle (2026-10-15)

Builder dump and task dump from one sync each (fresh client JVM, project defaults), canonical Ion / reflective text
(`prototype/compare-models.sh`). Counts are project files per model class; the builder produces no file where its
builder returns null (e.g. GradleExtensions for 9 projects, AnnotationProcessingModel for projects without processors).

## Mechanical group + GradleSourceSetModel (prototype `e389417`)

```
ExternalProject-1310207950: identical=287 different=0 missing=0 extra-in-tasks=0
GradleSourceSetModel-1524096923: identical=287 different=0 missing=0 extra-in-tasks=0
GradleTaskModel2056544129: identical=0 different=287 missing=0 extra-in-tasks=0
GradleExtensions1020684503: identical=278 different=0 missing=0 extra-in-tasks=0
ExternalTestsModel-1466771012: identical=287 different=0 missing=0 extra-in-tasks=0
IntelliJProjectSettings-533912855: identical=1 different=0 missing=0 extra-in-tasks=0
IntelliJSettings-1189957578: identical=1 different=0 missing=0 extra-in-tasks=0
DependencyAccessorsModel-1829422908: identical=287 different=0 missing=0 extra-in-tasks=0
VersionCatalogsModel-1186359053: identical=287 different=0 missing=0 extra-in-tasks=0
AnnotationProcessingModel-271669493: identical=256 different=0 missing=0 extra-in-tasks=0
JavaGradleManifestModel-1597729901: identical=287 different=0 missing=0 extra-in-tasks=0
RepositoryModels533226352: identical=287 different=0 missing=0 extra-in-tasks=0
ProjectRepositoriesModel1725750680: identical=287 different=0 missing=0 extra-in-tasks=0
KaptGradleModel-1145720784: identical=287 different=0 missing=0 extra-in-tasks=0
```

GradleTaskModel differs in every project only by the new `ideProjectModel` task entry (group "other", not public).
