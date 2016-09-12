import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;


public class GenerateNJstScript {

	
	public static void main(String[] args) {
		
		try {
			/* read in list of files*/
			String fileName = args[0];
                        String outputTre = args[1];
			LinkedList list = new LinkedList();
		    FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream din = new DataInputStream(fstream); 			
			BufferedReader in = new BufferedReader(new InputStreamReader(din));
			while (in.ready()) {
				String str = in.readLine();
				list.add(str);
			}
			in.close();
			System.out.println(startScript());
			System.out.println(runNJst(list, outputTre));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String runNJst(LinkedList list, String outputTre) {
		String str = "";
		str += "library(phybase)\n";
		str += "sptree<-\"((((Horse_:0.0600591763,Bat_:0.0458507286)0.7200000000:0.0088787922,Cow_:0.0544825708)0.8490000000:0.0063994873,Dog_:0.0428368647)0.8590000000:0.0092439717,Human_:0.0418065002,Mouse_:0.0810379731);\"\n";
		str += "spname<-species.name(sptree)\n";
		str += "nspecies<-length(spname)\n";

		str += "rootnode<-9\n";
		str += "nodematrix<-read.tree.nodes(sptree,spname)$node\n";
		str += "seq<-rep(1,nspecies)\n";
		str += "species.structure<-matrix(0,nspecies,nspecies)\n";
		str += "diag(species.structure)<-1\n";

		
		str += "nodematrix[,5]<-0.01\n";

		str += "ngene<-" + list.size() + "\n";
		str += "genetree<-rep(\"\",ngene)\n";
		
		Iterator itr = list.iterator();
		int count = 1;
		while (itr.hasNext()) {
			String tree = (String)itr.next();			
			str += "genetree[" + count + "] <- \"" + tree + "\";\n";
			count++;
		}

                str += "final_tree = NJst(genetree,spname, spname, species.structure)\n";
                str += "write(final_tree, file=\"" + outputTre + "\")\n";
		return str;
	}
	public static String startScript() {
		String str = "";
		str += "nancdist<-function(tree, taxaname)\n";
		str += "{\n";
		str += "ntaxa<-length(taxaname)\n";
		str += "nodematrix<-read.tree.nodes(tree,taxaname)$nodes\n";
		str += "if(is.rootedtree(nodematrix)) nodematrix<-unroottree(nodematrix)\n";
		str += "dist<-matrix(0, ntaxa,ntaxa)\n";
		str += "for(i in 1:(ntaxa-1))\n";
		str += "for(j in (i+1):ntaxa)\n";
		str += "{\n";
		str += "anc1<-ancestor(i,nodematrix)\n";
		str += "anc2<-ancestor(j,nodematrix)\n";
		str += "n<-sum(which(t(matrix(rep(anc1,length(anc2)),ncol=length(anc2)))-anc2==0, arr.ind=TRUE)[1,])-3\n";
		str += "if(n==-1) n<-0\n";
		str += "dist[i,j]<-n\n";
		str += "}\n";
		str += "dist<-dist+t(dist)\n";
		str += "z<-list(dist=as.matrix, taxaname=as.vector)\n";
		str += "z$dist<-dist\n";
		str += "z$taxaname<-taxaname\n";
		str += "z\n";
		str += "}\n";

		str += "NJst<-function(genetrees, taxaname, spname, species.structure)\n";
		str += "{\n";

		str += "ntree<-length(genetrees)\n";
		str += "ntaxa<-length(taxaname)\n";
		str += "dist <- matrix(0, nrow = ntree, ncol = ntaxa * ntaxa)\n";
			
		str += "for(i in 1:ntree)\n";
		str += "{\n";
		str += "genetree1 <- read.tree.nodes(genetrees[i])\n";
		str += "thistreetaxa <- genetree1$names\n";
		str += "ntaxaofthistree <- length(thistreetaxa)\n";
		str += "thistreenode <- rep(-1, ntaxaofthistree)\n";
		str += "dist1<-matrix(0,ntaxa,ntaxa)\n";
		str += "for (j in 1:ntaxaofthistree) \n";
		str += "{\n";
		str += "thistreenode[j] <- which(taxaname == thistreetaxa[j])\n";
		str += "if (length(thistreenode[j]) == 0) \n";
		str += "{\n";
		str += "print(paste(\"wrong taxaname\", thistreetaxa[j],\"in gene\", i))\n";
		str += "return(0)\n";
		str += "}\n";
		str += "}\n";
		str += "dist1[thistreenode, thistreenode]<-nancdist(genetrees[i],thistreetaxa)$dist\n";
		str += "dist[i,]<-as.numeric(dist1)\n";
		str += "}\n";

		str += "dist[dist == 0] <- NA\n";
		str += "dist2 <- matrix(apply(dist, 2, mean, na.rm = TRUE), ntaxa, ntaxa)\n";
		str += "diag(dist2) <- 0\n";
		str += "if (sum(is.nan(dist2)) > 0)\n"; 
		str += "{\n";
		str += "print(\"missing species!\")\n";
		str += "dist2[is.nan(dist2)] <- 10000\n";
		str += "}\n";
		str += "speciesdistance <- pair.dist.mulseq(dist2, species.structure)\n";

		str += "tree<-write.tree(nj(speciesdistance))\n";
		str += "node2name(tree,name=spname)\n";
		str += "}\n";
		return str;
	}
}

