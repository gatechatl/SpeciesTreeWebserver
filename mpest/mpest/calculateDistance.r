library(phybase)

findmissingspecies <- function(genename, spname)
{
result <- rep(TRUE,length(spname))
for(i in 1:length(genename))
{
y <- which(spname==genename[i])
if(length(y)>0) result[y]<-FALSE
}
return(spname[result])
}


x <- scan("output.tre", what="character", sep="=")
spname<-read.tree.string("output.tre")$name
mptree <- read.tree(file="output.tre")

spname <- mptree$tip

genetree<-read.tree("format_input.tre")
ntree<-length(genetree)

distance<-rep(-1, ntree)

#write("GeneTreeName,TreeDistance",file="treedist");
for(i in 1:ntree){
print(i)
genename <- sort(genetree[i][[1]]$tip.label)
deltip <- findmissingspecies(genename,spname)
x<-drop.tip(mptree,tip=deltip)
#distance[i]<-dist.topo(x,genetree[[i]])/(2*length(genename))
distance[i]<-dist.topo(x,genetree[[i]])
write(distance[i],file="newtreedist", append = TRUE);
write(length(deltip),file="numtipdrop", append = TRUE);
}

