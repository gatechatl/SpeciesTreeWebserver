library(phybase)
genetrees <- read.tree.string("./format_input.tre",format="phylip")$tree
ntree <- length(genetrees)
spname <- species.name(genetrees[1])
for(i in 2:ntree) spname <- c(spname,species.name(genetrees[i]))

speciesname <- scan ("spname.txt", what=character(0));
numsgenenodes <- scan("numgenenode.txt", what=numeric());
taxaname <- scan("taxaname.txt", what=character(0));

if (length(speciesname) == 0) {
    #spname<-species.name(genetrees[1])
    #nspecies<-length(spname)
    #taxaname <- spname
    #species.structure<-matrix(0,nspecies,nspecies)
    #diag(species.structure)<-1

    taxaname <- names(table(spname))
    nspecies<-length(taxaname)
    species.structure2<-matrix(0,nspecies,nspecies)
    diag(species.structure2)<-1
    output = star.sptree(genetrees, taxaname, taxaname, species.structure2,outgroup="W",method="nj")
    write("Ran Without SNA", file = "log");
} else {

    species.structure<-spstructure(numsgenenodes);
    output = star.sptree(genetrees, speciesname, taxaname, species.structure,outgroup="W",method="nj")
    write("Ran With SNA", file = "log");

}
write(output, file = "output.tre");
