# AGENTS.md — wallet

## What this repo is
Fork of **Eclipse EDC IdentityHub**. `settings.gradle.kts` still has `rootProject.name = "identity-hub"`
and the root `README.md` is upstream/stale — **trust build files and code, not the README**.
The wallet stores `did:web` credentials for the EDC and also acts as issuer (identity-hub +
issuer-service combined into one deployable to reduce component count).

Java, multi-module Gradle (Kotlin DSL). The vast majority of the tree mirrors upstream and is
irrelevant to Construct-X.

## Construct-X-specific code (this is what you almost always want)
Everything else is upstream. The only Construct-X-authored parts are:
- `launcher/con-x-wallet/` — the actual wallet runtime + `Dockerfile` (assembles upstream EDC BOMs)
- `extensions/con-x/dev-attestation/` — dev-only extension to freely shape a VC `credentialSubject`; **not for production** (see its README)
- `charts/wallet/` and `charts/issuer-wallet/` — Construct-X Helm charts
- `gradle.properties` → `con-x-edcVersion` and the launcher's `dev-attestation` wiring in `settings.gradle.kts`

## Two version numbers — do not confuse them
- `version=0.18.0-SNAPSHOT` in `gradle.properties` is the inherited upstream repo version.
- `con-x-edcVersion=0.17.0` is what actually matters: it pins the **published EDC artifact BOMs**
  (`identityhub-bom`, `issuerservice-bom`, sql feature BOMs, `vault-hashicorp`) that
  `launcher/con-x-wallet` consumes at runtime. Change this to bump the wallet.
- Docker image tag is derived as `wallet:<con-x-edcVersion>-1` (`releaseVersion = "$edcVersion-1"`).

## Build
The `super-user-seed-extension` git submodule is required and is **not** checked out by default:
```shell
git submodule update --init --recursive
./gradlew :launcher:con-x-wallet:dockerize   # builds shadowJar + docker image (from repo root)
```
`dockerize` also generates `build/docker/runtimeClasspath-dependencies.txt`, copied into the image as `/DEPENDENCIES`.

## Branching
- **`develop` is the Construct-X single source of truth** and the default branch — base all work on it.
- **`main` is kept in sync with the upstream project** (Eclipse EDC IdentityHub); don't develop on it. Upstream updates land in `main` and are periodically merged into `develop`.
- New features go on a branch cut from `develop` (e.g. `feat/…`, `chore/…`), then merged back into `develop`.

## Notes
- Identity here is `did:web` (unlike the constructx-edc connector, which uses a custom BPN format).
- The `.github/workflows/` are mostly inherited upstream EDC pipelines (`verify.yaml`, `nightly.yml`, release/publish); they are not Construct-X-specific.
