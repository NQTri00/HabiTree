#!/usr/bin/env bash
set -euo pipefail

output=${1:?Specify a new directory for standalone Android verification}
mkdir "$output"
output=$(realpath "$output")
repository="$output/maven"
revision=$(git rev-parse HEAD)

bash ./gradlew --no-daemon --max-workers=2 \
  publishHabitreePublicationToHabiTreeLocalRepository \
  :core-network:publishAndroidPublicationToHabiTreeLocalRepository \
  :core-network:publishKotlinMultiplatformPublicationToHabiTreeLocalRepository \
  :shared:publishAndroidPublicationToHabiTreeLocalRepository \
  :shared:publishKotlinMultiplatformPublicationToHabiTreeLocalRepository \
  "-PhabitreePublicationRepository=$repository"

for module in app catalog; do
  python3 -B tools/export_android_consumer.py --ref "$revision" --module "$module" \
    --output "$output/$module" --repository "$repository"
done

bash ./gradlew --no-daemon --max-workers=2 --project-dir "$output/app" \
  :app:testDebugUnitTest :app:assembleDebug
bash ./gradlew --no-daemon --max-workers=2 --project-dir "$output/catalog" \
  :catalog:assembleRelease :catalog:domain:jar :catalog:data:assembleRelease

printf '%s\n' "$revision" > "$output/source-revision.txt"
(
  cd "$repository"
  find . -type f -print0 | sort -z | xargs -0 sha256sum
) > "$output/MAVEN-SHA256SUMS.txt"
