library(phybase);
MyTree <- read.tree.string("input.tre.tre")$tree
write(MyTree, file="output.nex")

