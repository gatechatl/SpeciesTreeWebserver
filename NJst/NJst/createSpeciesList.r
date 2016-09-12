library(phybase);
genetrees <- read.tree.string("input.tre",format="phylip")$tree;
for (genetree in genetrees) {
    spname<-species.name(genetree);
    write(spname, "speciesnames.txt", append = TRUE);

}

