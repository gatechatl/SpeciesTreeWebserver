phylip.to.ape <- function(tree)

{

# Get the labels of the tips (sequences)

seqnames <- tree$tip.label

numseqs <- length(seqnames)

# Get the number of internal nodes in the tree:

numinternalnodes <- tree$Nnode

# Get the bootstrap values

bootstraps <- tree$edge.length

# Get the edges for the tree

edges <- tree$edge

# For each edge, find the node at the end of the edge (closer to the tips

# of the tree):

numedges <- nrow(edges)

numnodes <- numinternalnodes + numseqs

mybootstraps <- numeric(numinternalnodes)

for (i in 1:numedges)

{

startnode <- edges[i,1]

endnode <- edges[i,2]

# Make sure the end node is not a tip:

if (endnode > numseqs)

{

# Get the bootstrap value for this node:

bootstrap <- bootstraps[i]

mybootstraps[endnode] = bootstrap

}

}

mybootstraps2 <- mybootstraps[(numseqs+1):(numnodes)]

tree$node.label <- mybootstraps2



return(tree)

}

library(phybase);
library(ape);
genetrees <- read.tree.string("output.tre",format="phylip")$tree;
tr <- read.tree(text = genetrees[1]);

#plot.phylo(tr,cex=0.55)
#tree <- phylip.to.ape(tr) 
plot.phylo(tr,cex=0.55) 


