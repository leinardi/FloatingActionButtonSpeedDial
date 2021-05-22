# Releasing

1. Bump the `build_versions.version_name` property in `dependencies.gradle` based on Major.Minor.Patch naming scheme
2. Update `CHANGELOG.md` for the impending release.
3. Update the `README.md` with the new version.
4. `git commit -am "Prepare for release X.Y.Z"` (where X.Y.Z is the version you set in step 1)
5. `git push`
6. `./gradlew clean assembleRelease :library:publishReleasePublicationToSonatypeRepository`
7. Create a new release on Github
    1. Tag version `X.Y.Z` (`git tag -s X.Y.Z && git push --tags`)
    2. Release title `X.Y.Z`
    3. Paste the content from `CHANGELOG.md` as the description
    4. Upload the sample-release.apk
8. Create a PR from [master](../../tree/master) to [release](../../tree/release)
9. Visit [Sonatype](https://s01.oss.sonatype.org/#stagingRepositories) and Close and Release the artifact
10. Visit [Google Play Console](https://play.google.com/apps/publish/) and upload and publish the new APK
