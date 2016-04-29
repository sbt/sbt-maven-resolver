
```
git clone --no-hardlinks --branch 1.0.x ../sbt sbt-maven-resolver
cd sbt-maven-resolver
git filter-branch --index-filter 'git rm --cached -qr -- . && git reset -q $GIT_COMMIT -- sbt-maven-resolver' --prune-empty
git reset --hard
git gc --aggressive
git prune
```
