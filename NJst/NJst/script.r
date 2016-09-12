
##################################################################################
#
#    calculate the distance of two sequences on the basis of 
#	number of ancestors between two sequences
#
##################################################################################
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

#############################################################################
#
#        generate gene trees from a species tree
#
#############################################################################

library(phybase)
genetrees <- scan("./format_input.tre",what="character")

genetrees <- read.tree.string("./format_input.tre",format="phylip")$tree
ntree <- length(genetrees)
spname <- species.name(genetrees[1])
for(i in 2:ntree) spname <- c(spname,species.name(genetrees[i]))


#spname<-species.name(genetrees[1])

#nodematrix<-read.tree.nodes(genetrees[1],spname)$node

speciesname <- scan ("spname.txt", what=character(0));
numsgenenodes <- scan("numgenenode.txt", what=integer());
taxaname <- scan("taxaname.txt", what=character(0));
#nodematrix<-genetrees[1]$node;
#seq<-rep(1,nspecies);

#species.structure<-spstructure(numsgenenodes);

if (length(speciesname) == 0) {
        taxaname <- names(table(spname))
        nspecies<-length(taxaname)
        species.structure2<-matrix(0,nspecies,nspecies)
        diag(species.structure2)<-1
        output = NJst(genetrees,taxaname, taxaname, species.structure2)
        write("Ran Without SNA", file = "log");
} else {
        species.structure<-spstructure(numsgenenodes);
        output = NJst(genetrees,taxaname, speciesname, species.structure)
        write("Ran With SNA", file = "log");
}

#output = NJst(genetrees,spname, spname, species.structure)
write(output, file = "output.tre");
#write(output, file = outputFile);

