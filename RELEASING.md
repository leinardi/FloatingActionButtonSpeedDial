# Releasing

1. Bump the `build_versions.version_name_*` property in `speeddial.config-conventions.gradle` based on Major.Minor.Patch naming scheme
2. Update `CHANGELOG.md` for the impending release.
3. Update the `README.md` with the new version.
4. `git commit -am "Prepare for release View|Compose X.Y.Z"` (where X.Y.Z is the version you set in step 1)
5. `./gradlew spotlessApply check`
6. `./gradlew clean assembleRelease`
7. `git push`
8. `./gradlew :library-view:publishReleasePublicationToSonatypeRepository`
9. `./gradlew :library-compose:publishReleasePublicationToSonatypeRepository`
10. Create a new release on Github
     1. Tag View version `X.Y.Z` (`git tag -s view-X.Y.Z && git push --tags`)
     2. Tag Compose version `X.Y.Z` (`git tag -s compose-X.Y.Z && git push --tags`)
     3. Release title (`View X.Y.Z` or `Compose X.Y.Z`)
     4. Paste the content from `CHANGELOG.md` as the description
     5. Upload the sample-release.apk
11. Create a PR from [master](../../tree/master) to [release](../../tree/release)
12. Visit [Sonatype](https://s01.oss.sonatype.org/#stagingRepositories) and Close and Release the artifact
13. Visit [Google Play Console](https://play.google.com/apps/publish/) and upload and publish the new APK
