nancdist<-function(tree, taxaname)
{
ntaxa<-length(taxaname)
nodematrix<-read.tree.nodes(tree,taxaname)$nodes
if(is.rootedtree(nodematrix)) nodematrix<-unroottree(nodematrix)
dist<-matrix(0, ntaxa,ntaxa)
for(i in 1:(ntaxa-1))
for(j in (i+1):ntaxa)
{
anc1<-ancestor(i,nodematrix)
anc2<-ancestor(j,nodematrix)
n<-sum(which(t(matrix(rep(anc1,length(anc2)),ncol=length(anc2)))-anc2==0, arr.ind=TRUE)[1,])-3
if(n==-1) n<-0
dist[i,j]<-n
}
dist<-dist+t(dist)
z<-list(dist=as.matrix, taxaname=as.vector)
z$dist<-dist
z$taxaname<-taxaname
z
}
NJst<-function(genetrees, taxaname, spname, species.structure)
{
ntree<-length(genetrees)
ntaxa<-length(taxaname)
dist <- matrix(0, nrow = ntree, ncol = ntaxa * ntaxa)
for(i in 1:ntree)
{
genetree1 <- read.tree.nodes(genetrees[i])
thistreetaxa <- genetree1$names
ntaxaofthistree <- length(thistreetaxa)
thistreenode <- rep(-1, ntaxaofthistree)
dist1<-matrix(0,ntaxa,ntaxa)
for (j in 1:ntaxaofthistree) 
{
thistreenode[j] <- which(taxaname == thistreetaxa[j])
if (length(thistreenode[j]) == 0) 
{
print(paste("wrong taxaname", thistreetaxa[j],"in gene", i))
return(0)
}
}
dist1[thistreenode, thistreenode]<-nancdist(genetrees[i],thistreetaxa)$dist
dist[i,]<-as.numeric(dist1)
}
dist[dist == 0] <- NA
dist2 <- matrix(apply(dist, 2, mean, na.rm = TRUE), ntaxa, ntaxa)
diag(dist2) <- 0
if (sum(is.nan(dist2)) > 0)
{
print("missing species!")
dist2[is.nan(dist2)] <- 10000
}
speciesdistance <- pair.dist.mulseq(dist2, species.structure)
tree<-write.tree(nj(speciesdistance))
node2name(tree,name=spname)
}

library(phybase)
sptree<-"((((Horse_:0.0600591763,Bat_:0.0458507286)0.7200000000:0.0088787922,Cow_:0.0544825708)0.8490000000:0.0063994873,Dog_:0.0428368647)0.8590000000:0.0092439717,Human_:0.0418065002,Mouse_:0.0810379731);"
spname<-species.name(sptree)
nspecies<-length(spname)
rootnode<-9
nodematrix<-read.tree.nodes(sptree,spname)$node
seq<-rep(1,nspecies)
species.structure<-matrix(0,nspecies,nspecies)
diag(species.structure)<-1
nodematrix[,5]<-0.01
ngene<-5
genetree<-rep("",ngene)
genetree[1] <- "((((H:0.00402,C:0.00402):0.00304,G:0.00706):0.00929,O:0.01635):0.1,W:0.11635);";
genetree[2] <- "((((H:0.00402,G:0.00402):0.00304,C:0.00706):0.00929,O:0.01635):0.1,W:0.11635);";
genetree[3] <- "((((O:0.00402,C:0.00402):0.00304,G:0.00706):0.00929,H:0.01635):0.1,W:0.11635);";
genetree[4] <- "((((H:0.00402,C:0.00402):0.00304,G:0.00706):0.00929,O:0.01635):0.1,W:0.11635);";
genetree[5] <- "";
final_tree = NJst(genetree,spname, spname, species.structure)
write(final_tree, file="output.tre")

