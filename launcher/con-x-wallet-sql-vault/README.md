## con-x-wallet-sql-vault

# con-x-controlplane-postgresql-vault Module

This runtime is a variant of the con-x-wallet runtime, which is using the
[SqlVaultExtension](../../extensions/con-x/sql-vault/README.md) instead of the upstream's HashiCorp Vault extension in order to manage the handling of certain
secrets that the creators of the upstream repo considered important enough to be stored in a vault. Like for example cryptographic
keys and things like that.

Since handling and operating a Hashicorp vault in a professional way can be a somewhat demanding task, this runtime allows
you to choose an alternative, which may not be optimal under pure security considerations, but in exchange makes operating
a con-x wallet a bit less difficult.

This project is relying on a git submodule for the super-user-seed extension. In case the `extensions/super-user-seed-extension` folder is empty on your local system, please run 

```shell
git submodule update --init --recursive
```

In order to create a local docker image, please run (from the project root folder): 

```shell
./gradlew :launcher:con-x-wallet-sql-vault:dockerize
```