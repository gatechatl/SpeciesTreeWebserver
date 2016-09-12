library(ape);
MyTree <- read.nexus("genetree.tree.tre")
write.tree(MyTree, file="output.tre")

