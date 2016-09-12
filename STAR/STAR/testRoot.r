library(phybase)
bad = FALSE;
write(bad, file = "isroot");
genetrees <- read.tree.string("Output/50c8eef834e40/ZipOutput/GoodExampleForUnrootedBootstrap/gene2.tre.oneline.tre",format="phylip")$tree
ntree <- length(genetrees);
rooted = TRUE;
if (!exists("ntree") {
    rooted = FALSE;
}
if (ntree == 0) {
    rooted = FALSE;
}
for(i in 1:ntree) {
    if (!is.rootedtree(genetrees[2])) {
        rooted = FALSE;
    }
}
write(rooted, file = "isroot");
