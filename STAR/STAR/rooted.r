library(phybase)
genetrees <- read.tree.string("./format_input.tre",format="phylip")$tree;
ntree <- length(genetrees);
result = TRUE;
for (i in 1:ntree) {
    if (!is.rootedtree(genetrees[i])) {
        result = FALSE;
    }
}
if (result) {
    write(result, file = "rooted.txt");
}
