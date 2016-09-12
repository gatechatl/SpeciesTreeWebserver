library(ape)
speciestree <- read.tree('output.tre')
genetrees <- read.tree('format_input.tre')

label <- speciestree$tip.label

for (i in 1:length(genetrees)) {
    if (i < 100) {
    tree <- genetrees[[i]]
    genelabel <- tree$tip.label
    association <- character()
    for (j in genelabel) {
        if (j %in% label) association <- c(association, j)
    }
    association <- cbind(association, association)
    print(i)
    pdf(paste('GeneTree', i, '.pdf', sep=''), width = 15, height = 10)
    cophyloplot(speciestree, tree, assoc=association, length.line=2, space=80)
    dev.off()
    }
}
