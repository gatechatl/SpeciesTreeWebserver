library(phybase);
genetrees <- read.tree.string("format_input.tre",format="phylip")$tree;
for (genetree in genetrees) {
    spname<-species.name(genetree);
    write(spname, "speciesname.txt", append = TRUE);

}

