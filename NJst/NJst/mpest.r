library(phybase)
genetrees <- scan("./input.tre",what="character")
spname<-species.name(genetrees[1])
nspecies<-length(spname)
taxaname <- spname
species.structure<-matrix(0,nspecies,nspecies)
diag(species.structure)<-1
output = star.sptree(genetrees, spname, taxaname, species.structure,outgroup="W",method="nj")
write(output, file = ".output.tre");
