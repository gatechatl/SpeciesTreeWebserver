library(ape);
MyTree <- read.nexus("output.nex")
write.tree(MyTree, file="output.tre")

