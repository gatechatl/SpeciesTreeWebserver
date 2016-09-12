/*
 *  TripleDIstance Calculation
 *  based on code from mpest 1.0
 *
 *  copyright 2009-2013
 *
 *  Liang Liu
 *  Department of Organismic and Evolutionary Biology
 *  Harvard University
 *
 *  lliu@oeb.harvard.edu
 *
 *  Modified by 
 *  Timothy Shaw
 *  Institute of Bioinformatics
 *  University of Georgia
 *
 *  gatechatl@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details (www.gnu.org).
 *
 * 
 */

#include	"TripleDist.h"
#               if defined (MPI_ENABLED)
#include        <mpi.h>
#               endif

int 		addNode (Tree *tree, int fromnode, int fromfather, int tonode);
int 		AddToPrintString (char *tempStr);
int 		Algorithm (int **triple, FILE *outfile);
void 		copyTree(Tree *from, Tree *to);
int 		deleteNode (Tree *tree, int inode);
int 		findNgenesandNtaxa (FILE *fTree);
int 		findNameNumber (Tree *tree);
//int		findQuartet (int node1, int node2, int node3, int *location);
int 		findTriple (int node1, int node2, int node3, long int *location);
void 		findOffsprings (int *offsprings, Tree *tree, int inode);
void 		findInternalOffsprings(int *offsprings, Tree *tree, int inode);
int 		findOutgroup (Tree *tree);
int 		logbinomialP (int n, int x, double p, double *logp);
double 		logbinomP (int n, int x, double p, double *logp);
int 		logLikelihood (int **triple, Tree *tree, double *loglike);
int 		logLikelihoodQuartet (int **quartet, Tree *tree, double *loglike);
int 		maximizeaBrlen (int **triple, Tree *tree);
void 		MoveBrlens (Tree *tree);
int 		MoveNode (Tree *tree);
void		MrBayesPrint (char *format, ...);
void 		PrintHeader (void);
void 		printSptree (void);
int 		PrintState (int round, FILE *fout, int addend);
int 		PrintTree (Tree *tree, int inode, int showBrlens, int showTheta, int isRooted);

void		printQuartet (FILE *outfile); /*Tim Added*/

void 		printTriples (FILE *outfile);
void 		randomTree(Tree *tree);
void 		randomVector (int *array, int number);
int 		rearrangeNodes (Tree *tree, int fromnode, int tonode);
int 		ReadaTree (FILE *fTree,Tree *tree);
void 		*SafeMalloc(size_t s);
int 		SaveSprintf(char **target, int *targetLen, char *fmt, ...);
int 		swapNodes (Tree *tree, int inode, int jnode);
double 		tripleDist (Tree *tree1, Tree *tree2);
int		quartet (Tree *tree, int ntrees); /*Tim Added*/
int 		triples (Tree *tree, int ntrees);

int		quartetInatree (int **quartet, int **quartetOnce, Tree *tree); /* Tim added*/
int 		triplesInatree (int **triple, Tree *tree);

double 		findBranchLength(int node1, int node2, int node3, int node4, Tree *tree, long int *location);
int 		findQuartet(int n1, int n2, int n3, int n4, long int *location); 
void 		labelAllFather(int *offsprings, Tree *tree, int inode, int index);
void 		mergeOffsprings(int *offsprings1, int *offsprings2, int *combinedoffsprings, Tree *tree);


double 		calcBrLen(int *offsprings1, int *offsprings2, int *offsprings3, int *offsprings4, Tree *tree, int maxNode);
double 		calcBrLen2(int *offsprings1, int *offsprings2, int *offsprings3, int *offsprings4, Tree *tree, int maxNode, int maxNode13, int maxNode14);
double 		calcBrLenMerge3(Tree *tree, int maxNode12, int maxNode123, int maxNode124);
double 		calcBrLenMerge2(int maxNode12, int maxNode34, Tree *tree);

void 		WriteTreeToFile (Tree *tree, int inode, int showBrlens, int showTheta, int isRooted);
void		WritePhylipTreeToFile (Tree *tree, int inode, int showBrlens, int showTheta, int isRooted);
int		PrintPhylipTree(Tree *tree, int inode, int showBrlens, int showTheta, int isRooted);

Tree 		sptree;
Tree		gtree[NGENE];
int		nGene = 0;
int		**triplematrix; /*store the frequency of species tree triples among gene trees*/

int 		**quartetmatrixOnce; /*store the frequency of species tree quartet among gene trees */
int 		**quartetmatrix; /*store the frequency of species tree quartet among gene trees */
int		**quartetindex; /*store the index for each combination*/

char		spacer[10]="  ";
char		*printString;                /* string for printing to a file                */
size_t		printStringSize;             /* length of printString                        */
long int	seed=0;
int 		usertree=0;		/* 1: use the usertree as the starting tree */
int		taxanodenumber[NTAXA];
char		taxanames[NTAXA][50];
int 		totaltaxa;
double		curLn;
int		NChoose4Count = 0;

void 		printFQuartet();
void 		printFIndex();
void 		inittwiddle(int m, int n, int *p);
int 		twiddle(int *x, int *y, int *z, int *p);
int 		containsValue(int *list, int value, int size);
#       if defined (MPI_ENABLED)
int             proc_id;
int             num_procs;
#       endif



int main (int argc, char *argv[])
{
	int i, j, n, index=0, ngene, distance; 
	FILE *fTree, *fout, *fin; //*ferror;
	time_t t;
	struct tm *current;
	char genetreefile[50], outfile[50];


#       if defined (MPI_ENABLED)
	MPI_Init(&argc,&argv);
        MPI_Comm_rank(MPI_COMM_WORLD,&proc_id);
        MPI_Comm_size(MPI_COMM_WORLD,&num_procs);
        if( proc_id == 0) PrintHeader();
#       else
        //PrintHeader();
#       endif

	
	fin = (FILE*)gfopen(argv[1],"r");
	fscanf(fin,"%s%d%ld%d%d", genetreefile, &distance, &seed, &ngene, &(sptree.ntaxa));
	sprintf(outfile, "%s.tre", genetreefile);

	/*the correspondence between species and individual sequences*/
	for(i=0; i<sptree.ntaxa; i++)
	{
		fscanf(fin,"%s%d", sptree.nodes[i].taxaname, &n);
		for(j=0; j<n; j++)
		{
			fscanf(fin,"%s", taxanames[index]);	
			/*defines the species number that each taxon belongs to*/ 
			taxanodenumber[index++] = i;
		}
	}	
	totaltaxa = index;

#       if defined (MPI_ENABLED)
	if (proc_id == 0) fout = (FILE*)gfopen(outfile,"w");
#       else
	fout = (FILE*)gfopen(outfile,"w");
#	endif

        //printf("%s\n", genetreefile);
	fTree = (FILE*)gfopen(genetreefile,"r");


	/*set seed*/
	if(seed <= 0)
	{
		time(&t);
		current = localtime(&t);
#	if defined (MPI_ENABLED)
		seed = 11*current->tm_hour + 111*current->tm_min + (proc_id+1)*1111*current->tm_sec + 123;
#	else
		seed = 11*current->tm_hour + 111*current->tm_min + 1111*current->tm_sec + 123;
#	endif
		SetSeed(seed);
	}
	else
		SetSeed(seed);

	fscanf(fin,"%d", &usertree);

	if(usertree > 0)
	{
		ReadaTree(fin, &sptree);
		/*for(i=0; i<2*sptree.ntaxa-1; i++)
			sptree.nodes[i].brlens = 7.0;
		sptree.nodes[sptree.root].brlens = 0.0;*/
	}

	fclose (fin);

	nGene = findNgenesandNtaxa(fTree);


	if(nGene > ngene)
		nGene = ngene;

	for(i=0; i<nGene; i++)
	{
		ReadaTree(fTree, &gtree[i]);
		/*if(i==0) 
		{
			for (j=0; j<gtree[0].ntaxa; j++) {
				strcpy(name[j], gtree[i].nodes[j].taxaname);
			}
			sptree.ntaxa = gtree[0].ntaxa;
		}
		else {
			for (j=0; j<gtree[i].ntaxa; j++) {
				for (k=0; k<sptree.ntaxa; k++) {
					if (!strcmp(gtree[i].nodes[j].taxaname, name[k])) {
						break;
					}
				}
				
				if(k == sptree.ntaxa) {
					strcpy(name[sptree.ntaxa], gtree[i].nodes[j].taxaname);
					sptree.ntaxa++;
				}
							
			}
		}*/
	}
	

	if(findNameNumber(gtree) == ERROR)
	{
		printf("Errors in findNameNumber function\n");
		exit(-1);
	}

	/*summarize triples in gene trees and the result is stored in triplematrix*/
	/*if(triples (gtree, nGene) == ERROR)
	{
		printf("Errors in the triples function\n");
		exit(-1);
	}*/

	/*free memory*/
	free(triplematrix[0]);
	free(triplematrix);

	free(quartetmatrix[0]);
	free(quartetmatrix);

	free(quartetmatrixOnce[0]);
	free(quartetmatrixOnce);
	//free(quartetindex[0]);

	free(quartetindex[0]);	
	free(quartetindex);
	
  	fclose(fTree);

	//printf("%d\n", nGene);
	for(i=1; i<nGene; i++) {
		printf("%f\n", tripleDist(&gtree[0], &gtree[i]));

        }

/*	for(i=0; i<nGene/2; i++)
		printf("%d\t%d\t%f\n",2*i+1, 2*i+2, tripleDist(&gtree[2*i], &gtree[2*i+1]));*/
	// add calculation of tripleDist
/*	for(i=0; i<nGene; i++)
	{
		double dist = tripleDist(&gtree[i], &sptree);
		printf("dist: %4.10f\t%d\t%d\n", dist, sptree.ntaxa, gtree[i].ntaxa);
	}	
	*/

#	if defined (MPI_ENABLED)
	if (proc_id == 0) fclose (fout);
        MPI_Finalize();
#	else
	fclose(fout);
#       endif
  	
  	return(1);
}

/**
 * Calculates the loglikelihood for a quartet
 * By Tim Shaw
 */
int logLikelihoodQuartet (int **quartet, Tree *tree, double *loglike)
{
	int i, j;
	int offsprings1[NTAXA], n1;
	long int location[2];
	double p, brlens, logp, sumlogp = 0;
	int *array;

	if(DEBUG)
		array = (int*)calloc(sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)/6, sizeof (int));
 	
		*loglike = 0.0;
		n1= 0;
		findOffsprings (offsprings1, tree, tree->root);
		for(j=0; j<tree->ntaxa; j++)
		{
			if (offsprings1[j] == 1)
				offsprings1[n1++] = j;
		}		
		// iterate through all the possible offsprings1 n1 choose 2
		int node1, node2, node3, node4;
		int M = 4;
		int N2 = sptree.ntaxa;
		int x2, y2, z2, p2[N2+2], b2[N2];		

		int m;
		
		inittwiddle(M, N2, p2);		

		for (m = 0; m != N2 - M; m++) {
			b2[m] = 0;
		}
		while (m != N2) {
			b2[m++] = 1;
		}	
		
	
		// grab node3 and node4		
		node1 = -1; node2 = -1; node3 = -1; node4 = -1;		
		for (m = 0; m < N2; m++) {
			if (b2[m] == 1) {
				if (node1 == -1) {
					node1 = offsprings1[m];
				} else if (node2 == -1) {
					node2 = offsprings1[m];
				} else if (node3 == -1) {
					node3 = offsprings1[m];
				} else {
					node4 = offsprings1[m];
				}
			}
		}
		//printf("N2: %d\n", N2);
		//printf("Try this node1: %d Node2 %d Node3 %d Node4 %d \n\n", node1, node2, node3, node4);
		brlens = findBranchLength (node1, node2, node3, node4, tree, location); // test those three locations
		//printf("brlens: %d\n", brlens);
		p = 1-exp(-brlens)*2/3;
		//if(logbinomialP (quartetmatrix[location[0]][3],quartetmatrix[location[0]][location[1]],p,&logp) == ERROR) {
		/*if(logbinomP (quartetmatrix[location[0]][3],quartetmatrix[location[0]][location[1]],p,&logp) == ERROR) {
			printf("Errors in logbinomialP\n");
			return (ERROR);
		}*/					
		//*loglike += logp;
		//sumlogp += logp;
		*loglike += logbinomP (quartetmatrix[location[0]][3],quartetmatrix[location[0]][location[1]],p,&logp);
		sumlogp += logbinomP (quartetmatrix[location[0]][3],quartetmatrix[location[0]][location[1]],p,&logp);
		//printf("\np: %4.10f logp: %4.10f loglike: %4.10f\n\n", p, logp, sumlogp);		
		// lookup frequency table
		// calculate p
		//printf("nodes %d %d %d %d location %ld %ld\n", node1, node2, node3, node4, location[0], location[1]);

		while(!twiddle(&x2, &y2, &z2, p2)) {
			b2[x2] = 1;
			b2[y2] = 0;

			node1 = -1; node2 = -1; node3 = -1; node4 = -1;
			for (m = 0; m < N2; m++) {
				if (b2[m] == 1) {
					if (node1 == -1) {
						node1 = offsprings1[m];
					} else if (node2 == -1) {
						node2 = offsprings1[m];
					} else if (node3 == -1) {
						node3 = offsprings1[m];
					} else {
						node4 = offsprings1[m];
					}
				}
			}	
			
			brlens = findBranchLength (node1, node2, node3, node4, tree, location); // test those three locations					
			//printf("quartetmatrix[location[0]][3]: %d\n", quartetmatrix[location[0]][3]);
			//printf("quartetmatrix[location[0]][location[1]]: %d\n", quartetmatrix[location[0]][location[1]]);
			//printf("brlens: %4.2f\n", brlens);	
			p = 1-exp(-brlens)*2/3;
			//if(logbinomialP (quartetmatrix[location[0]][3],quartetmatrix[location[0]][location[1]],p,&logp) == ERROR) {
			//
			//	printf("Errors in logbinomialP\n");
			//	return (ERROR);
			//}		
			//*loglike += logp;	
			//sumlogp += logp;
			*loglike += logbinomP (quartetmatrix[location[0]][3],quartetmatrix[location[0]][location[1]],p,&logp);
			sumlogp += logbinomP (quartetmatrix[location[0]][3],quartetmatrix[location[0]][location[1]],p,&logp);
			//printf("\np: %4.10f logp: %4.10f loglike: %4.10f\n\n", p, logp, sumlogp);
			// lookup frequency table
			// calculate p			
			//printf("nodes %d %d %d %d location %ld %ld\n", node1, node2, node3, node4, location[0], location[1]);

		}
	if(DEBUG)
	{
		for(i=0; i<sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)/6; i++)
			if(array[i] != 1)
			{
				//printf("Errors in findTriple\n");
				printf("Errors in findQuartet\n");
				return(ERROR);
			}
	}

	//if(ntriples != (tree->ntaxa * (tree->ntaxa-1) * (tree->ntaxa-2) / 24))
	//	return (ERROR);

	return(NO_ERROR);
}


/**
 * This is for triple method
 * possibly delete this? 
 */
int logLikelihood (int **triple, Tree *tree, double *loglike)
{
	int i, j, k, w, son0, son1, father, cnode, ntriples=0;
	int offsprings0[NTAXA], offsprings1[NTAXA], offsprings2[NTAXA], taxa0[NTAXA], taxa1[NTAXA], taxa2[NTAXA], n0, n1, n2;
	long int location[2];
	double p, brlens, logp;
	int *array;

	if(DEBUG)
		array = (int*)calloc(sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)/6, sizeof (int));
 	
	*loglike = 0.0;
	for(i=tree->ntaxa; i<2*tree->ntaxa-1; i++)
	{
		if(i == tree->root)
			continue;
		else
		{
			brlens = tree->nodes[i].brlens;
			n0 = n1 = 0;
			for(j=0; j<tree->ntaxa; j++)
			{
				offsprings0[j] = 0;
				offsprings1[j] = 0;
			}

			son0 = tree->nodes[i].sons[0];
			son1 = tree->nodes[i].sons[1];
			findOffsprings (offsprings0, tree, son0);
			findOffsprings (offsprings1, tree, son1);

			for(j=0; j<tree->ntaxa; j++)
			{
				if (offsprings0[j] == 1)
					taxa0[n0++] = j;
				if (offsprings1[j] == 1)
					taxa1[n1++] = j;
			}

			cnode = i;
			father = tree->nodes[i].father;
			while (father != -1)
			{
				for(j=0; j<tree->ntaxa; j++)
					offsprings2[j] = 0;
						
				n2 = 0;
				p = 1-exp(-brlens)*2/3;

				if(tree->nodes[father].sons[0] == cnode)
					findOffsprings (offsprings2, tree, tree->nodes[father].sons[1]);
				else
					findOffsprings (offsprings2, tree, tree->nodes[father].sons[0]);

				for(j=0; j<tree->ntaxa; j++)
				{
					if (offsprings2[j] == 1)
						taxa2[n2++] = j;
				}

				for(j=0; j<n0; j++)
				{
					for(k=0; k<n1; k++)
						for(w=0; w<n2; w++)
						{
							//findQuartet (taxa0[j], taxa1[k], taxa2[w], location);
							findTriple (taxa0[j], taxa1[k], taxa2[w], location);
							if (DEBUG)
							{
								array[location[0]] = 1;
								/*printf("location %d %d %d %ld %ld %f %d %d\n",taxa0[j],taxa1[k],taxa2[w],location[0],location[1],brlens,triplematrix[location[0]][3],triplematrix[location[0]][location[1]]);*/
							}
							if(logbinomialP (triplematrix[location[0]][3],triplematrix[location[0]][location[1]],p,&logp) == ERROR)
							//if(logbinomialP (quartetmatrix[location[0]][3],quartetmatrix[location[0]][location[1]],p,&logp) == ERROR)
							{
								printf("Errors in logbinomialP\n");
								return (ERROR);
							}
							/*printf("logp %f %d %d %d\n",logp, taxa0[j],taxa1[k],taxa2[w]);*/
							*loglike += logp;
						}
				}
				brlens += tree->nodes[father].brlens;
				cnode = father;
				father = tree->nodes[father].father;

				/*check the number of triples*/
				ntriples += n0 * n1 * n2;
			}					
		}
	}

	if(DEBUG)
	{
		for(i=0; i<sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)/6; i++)
			if(array[i] != 1)
			{
				//printf("Errors in findTriple\n");
				printf("Errors in findQuartet\n");
				return(ERROR);
			}
	}

	if(ntriples != (tree->ntaxa * (tree->ntaxa-1) * (tree->ntaxa-2) / 6))
		return (ERROR);

	return(NO_ERROR);
}

/**
 * calculating the log binomial p
 *
 */
int logbinomialP (int n, int x, double p, double *logp)
{

	/**logp = 0.0;

	if(p>1.0 || p<0.0 || x > n)
	{
		printf("probability cannot be %f\n",p); 
		return(ERROR);
	}

	for(i=x+1; i<=n; i++)
		(*logp) += log(i);
	for(i=1; i<=(n-x); i++)
		(*logp) -= log(i);
	if(p == 1)
		(*logp) += x*log(0.999999) + (n-x)*log(1-0.999999);
	else
		(*logp) += x*log(p) + (n-x)*log(1-p);*/

	if(p == 1) p = 0.99999999;
	if(p == 0) p = 0.00000001;
	
	//*logp = x*log(p) + (n-x)*log((1-p)/2);

	double d = x*log(p);
	double y = (n-x)*log((1-p)/2);
	*logp = d + y;
	//printf("n: %d x: %d p: %4.10f logp value: %4.10f\n", n, x, p, x*log(p) + (n-x)*log((1-p)/2));
	//printf("n: %d x: %d p: %4.10f x*log(p) value: %4.10f\n", n, x, p, x*log(p));
	//printf("n: %d x: %d p: %4.10f (n-x)*log((1-p)/2) value: %4.10f\n", n, x, p, (n-x)*log((1-p)/2));	
	return (NO_ERROR);
}

/**
 * Calculates the binomial p
 * By Tim Shaw
 */
double logbinomP (int n, int x, double p, double *logp)
{

	if(p == 1) p = 0.99999999;
	if(p == 0) p = 0.00000001;
	

	double d = x*log(p);
	double y = (n-x)*log((1-p)/2);
	return d + y;

}

/**
 * Main algorithm running the likelihood.
 */
//int Algorithm (int **triple, FILE *outfile)
int Algorithm (int **completematrix, FILE *outfile)
{
	double oldloglike, diff;
	int i, round=1, sample = 1000, totalround = MAXROUND, stop = 0, noincreasing=0, accept = 0;
	Tree oldsptree;
	time_t starttime, endtime;
	clock_t start, end;
     	double cpu_time_used;

#	if defined (MPI_ENABLED)
	int ierror, tag=0;
	int *noincreasing_id;
	MPI_Status status;

	noincreasing_id = (int*)malloc((num_procs+1)*sizeof(int));
#	endif

	//cpu time
	start = clock();

	if(usertree == 0) {
		printf("\n\n********Generating Random Tree*******\n\n");	
		randomTree (&sptree);
	}

	copyTree (&sptree, &oldsptree);
	for(i=0; i<sptree.ntaxa; i++)
		strcpy(oldsptree.nodes[i].taxaname, sptree.nodes[i].taxaname);


	
	//if(logLikelihood (triple, &sptree, &curLn) == ERROR)
	if (logLikelihoodQuartet (completematrix, &sptree, &curLn) == ERROR)
	{
		printf("Errors in loglikelihood\n");
		return (ERROR);
	}
	
	if (PrintState (round, outfile, 0) == ERROR)
	{
        	MrBayesPrint ("%S   Errors in PrintState.\n", spacer);
               	return (ERROR);
     	}

#	if defined (MPI_ENABLED)
	if(proc_id == 0)  starttime = time(0);
#	else
	starttime = time(0);
#	endif

	while (round < totalround)
	{

		/*copy likelihood and tree*/
		copyTree (&sptree, &oldsptree);
		oldloglike = curLn;
		
		if(MoveNode (&sptree) == ERROR)
		{
			printf("Errors in MoveNode\n");
			return (ERROR);
		}
		//if(logLikelihood (triple, &sptree, &curLn) == ERROR)
		if(logLikelihoodQuartet (completematrix, &sptree, &curLn) == ERROR)
		{
			printf("Errors in logLikelihood\n");
			return (ERROR);
		}
		diff = curLn - oldloglike;

		if(diff < 0)
		{
			noincreasing++;
			curLn = oldloglike;
			copyTree (&oldsptree, &sptree);
		}
		else
		{
			if(diff > 0)
				noincreasing = 0;			
			accept++;
		}
		
		round ++;
		if((round % sample) == 0 || stop == NUM_NOCHANGE)
		{
			if (PrintState (round, outfile, 0) == ERROR)
			{
                        MrBayesPrint ("%S   Errors in PrintState.\n", spacer);
                        return (ERROR);
			}
		}

#	if defined (MPI_ENABLED)
   	ierror = MPI_Barrier (MPI_COMM_WORLD);
        if (ierror != MPI_SUCCESS)
                {
                MrBayesPrint ("%s   Problem at chain barrier.\n", spacer);
                return ERROR;
                }

	if (proc_id != 0)
	{
		ierror = MPI_Send(&noincreasing, 1, MPI_INT, 0, tag, MPI_COMM_WORLD);
                if (ierror != MPI_SUCCESS)
                {
                        MrBayesPrint ("%s   Problem finding processor with chain to print.\n", spacer);
                        return (ERROR);
                }
	}
	else
	{
		noincreasing_id[0] = noincreasing;
		for(i=1; i<num_procs; i++)
        	{
                        ierror = MPI_Recv(&noincreasing_id[i], 1, MPI_INT, i, tag, MPI_COMM_WORLD, &status );
                        if (ierror != MPI_SUCCESS)
                        {
                        MrBayesPrint ("%s   Problem finding processor with chain to print.\n", spacer);
                        return (ERROR);
                        }
		}
		for (i=0; i<num_procs; i++)
		{
			if (noincreasing_id[i] < NUM_NOCHANGE) 
				break;
		}
		if (i == num_procs) stop = 1;
	}
	MPI_Bcast (&stop, 1, MPI_INT, 0, MPI_COMM_WORLD);

#	else
		if (noincreasing >= NUM_NOCHANGE)
			stop = 1;
#	endif
		if(stop == 1)	break;		
	}
	
	
	if (PrintState (round, outfile, 1) == ERROR)
        {
    		MrBayesPrint ("%S   Errors in PrintState.\n", spacer);
              	return (ERROR);
      	}


#       if defined (MPI_ENABLED)
        if(proc_id == 0)
        {
		endtime = time(0);
	        if (difftime (endtime, starttime) > 2.0)
        	{
                fprintf (outfile, "  [Analysis completed in %.0f seconds]\n", difftime(endtime, starttime));
                printf("\t\t\tAnalysis is completed in %.0f seconds\n", difftime(endtime, starttime));
        	}
        	else if (difftime (endtime, starttime) >= 1.0)
        	{
                fprintf (outfile, "  [Analysis completed in 1 second]\n");
                printf("\t\t\tAnalysis completed in 1 second\n");
        	}
       	 	else
        	{
                fprintf (outfile, "  [Analysis completed in less than 1 second]\n");
                printf("\t\t\tAnalysis completed in less than 1 second\n");
        	}
	}
#	else
	//cpu time
	end = clock();
     	cpu_time_used = ((double) (end - start)) / CLOCKS_PER_SEC;

	endtime = time(0);
	if (difftime (endtime, starttime) > 2.0)
	{
		fprintf (outfile, "  [Analysis completed in %.0f seconds]\n", difftime(endtime, starttime));
		printf("\t\t\tAnalysis is completed in %.0f seconds (cpu time is %.0f seconds)\n", difftime(endtime, starttime), cpu_time_used);
	}
	else if (difftime (endtime, starttime) >= 1.0)
	{
		fprintf (outfile, "  [Analysis completed in 1 second]\n");
		printf("\t\t\tAnalysis completed in 1 second\n");
	}
	else
	{
		fprintf (outfile, "  [Analysis completed in less than 1 second]\n");
		printf("\t\t\tAnalysis completed in less than 1 second\n");
	}
#	endif

	return (NO_ERROR);
}
/**
 *  A simple printout for the quartet index
 *  By Tim Shaw
 **/
void printFIndex() 
{

	int j, k;
	long int nquartet = sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)*(sptree.ntaxa-3)/24;

	printf("  Print Index Matrix\n");
	
	printf("\n  3. quartet matrix\n");
	printf("  first\t\tsecond\t\tthird\t\tsum\n");
	for(j=0;j<nquartet;j++)
	{
		printf("  ");
		for(k=0;k<sptree.ntaxa;k++)
			printf("%d ", quartetindex[j][k]);
		printf("\n");
	}	
	
	
}
/**
 *  A simple printout for the quartet matrix
 *  By Tim Shaw
 **/
void printFQuartet() 
{

	int j, k;
	long int nquartet = sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)*(sptree.ntaxa-3)/24;

	printf("  2. summary of the dataset\n");
	printf("  \t\tntaxa\t\tnmissing\n");
	for(j=0;j<nGene;j++)
	{
		printf("  gene%d\t\t%d\t\t%d\n", j, gtree[j].ntaxa, totaltaxa-gtree[j].ntaxa);
	}
	
	printf("\n  3. quartet matrix\n");
	printf("  first\t\tsecond\t\tthird\t\tsum\n");
	for(j=0;j<nquartet;j++)
	{
		printf("%d-ID: ", j);
		for(k=0;k<sptree.ntaxa;k++) {
                        printf("%d ",quartetindex[j][k]);
		}
		printf("---index---");
		for(k=0;k<4;k++)
			printf("%d ",quartetmatrix[j][k]);
		printf("\n");
	}	
	
	printf("\n  4. quartet matrix Once\n");
	printf("  first\t\tsecond\t\tthird\t\tsum\n");
	for(j=0;j<nquartet;j++)
	{
		printf("%d-ID: ", j);
		for(k=0;k<sptree.ntaxa;k++) {
                        printf("%d ",quartetindex[j][k]);
		}
		printf("---index---");
		for(k=0;k<4;k++)
			printf("%d ",quartetmatrixOnce[j][k]);
		printf("\n");
	}	
	
}

void printQuartet (FILE *outfile) 
{

	int j, k;
	long int nquartet = sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)*(sptree.ntaxa-3)/24;

	fprintf(outfile, "  2. summary of the dataset\n");
	fprintf(outfile, "  \t\tntaxa\t\tnmissing\n");
	for(j=0;j<nGene;j++)
	{
		fprintf(outfile, "  gene%d\t\t%d\t\t%d\n", j, gtree[j].ntaxa, totaltaxa-gtree[j].ntaxa);
	}
	
	fprintf(outfile, "\n  3. quartet matrix\n");
	fprintf(outfile, "  first\t\tsecond\t\tthird\t\tsum\n");
	for(j=0;j<nquartet;j++)
	{
		fprintf(outfile, "  ");
		for(k=0;k<4;k++)
			fprintf(outfile, "%d\t\t",quartetmatrix[j][k]);
		fprintf(outfile,"\n");
	}	
	
	
}

void printFTriples ()
{
	int j, k;
	long int ntriples = sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)/6;

	printf("  2. summary of the dataset\n");
	printf("  \t\tntaxa\t\tnmissing\n");
	for(j=0;j<nGene;j++)
	{
		printf("  gene%d\t\t%d\t\t%d\n", j, gtree[j].ntaxa, totaltaxa-gtree[j].ntaxa);
	}
	
	printf("\n  3. triple matrix\n");
	printf("  first\t\tsecond\t\tthird\t\tsum\n");
	for(j=0;j<ntriples;j++)
	{
		printf("  ");
		for(k=0;k<4;k++)
			printf("%d\t\t",triplematrix[j][k]);
		printf("\n");
	}
}
void printTriples (FILE *outfile)
{
	int j, k;
	long int ntriples = sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)/6;

	fprintf(outfile, "  2. summary of the dataset\n");
	fprintf(outfile, "  \t\tntaxa\t\tnmissing\n");
	for(j=0;j<nGene;j++)
	{
		fprintf(outfile, "  gene%d\t\t%d\t\t%d\n", j, gtree[j].ntaxa, totaltaxa-gtree[j].ntaxa);
	}
	
	fprintf(outfile, "\n  3. triple matrix\n");
	fprintf(outfile, "  first\t\tsecond\t\tthird\t\tsum\n");
	for(j=0;j<ntriples;j++)
	{
		fprintf(outfile, "  ");
		for(k=0;k<4;k++)
			fprintf(outfile, "%d\t\t",triplematrix[j][k]);
		fprintf(outfile,"\n");
	}
}

int PrintState (int round, FILE *outfile, int addend)
{
	int i;
	char buffer[30];
  	struct timeval tv;
  	time_t curtime;

#	if defined (MPI_ENABLED)
	double 		loglike_id, maxloglike;
	MPI_Status  	status;
	int 		ierror, nErrors=0, sumErrors, tag, maxproc;

        tag = 0;
        if(proc_id != 0)
        {
            	ierror = MPI_Send (&curLn, 1, MPI_DOUBLE, 0, tag, MPI_COMM_WORLD );
            	if (ierror != MPI_SUCCESS)
                        {
                        MrBayesPrint ("%S   Problem finding processor with chain to print.\n", spacer);
                        nErrors++;
                        }
        }
        else
        {
		/*print to screen*/
                printf("%s round %d  Proc0: %f ", spacer, round, curLn);
		maxloglike = curLn;
		maxproc = 0;
                for(i=1; i<num_procs; i++)
                {
                        ierror = MPI_Recv(&loglike_id, 1, MPI_DOUBLE, i, tag, MPI_COMM_WORLD, &status );
                        if (ierror != MPI_SUCCESS)
                        {
                        MrBayesPrint ("%s   Problem finding processor with chain to print.\n", spacer);
                        nErrors++;
                        }
                        printf("Proc%d:%f ", i, loglike_id);

			if (loglike_id > maxloglike)
			{
				maxloglike = loglike_id;
				maxproc = i;
			}	
                }
                printf("complete...\n"); 
	}

	ierror = MPI_Bcast (&maxproc, 1, MPI_INT, 0, MPI_COMM_WORLD);
	if (ierror != MPI_SUCCESS)
                        {
                        MrBayesPrint ("%s   Problem finding processor with chain to print.\n", spacer);
                        nErrors++;
                        }

	MPI_Allreduce (&nErrors, &sumErrors, 1, MPI_INT, MPI_SUM, MPI_COMM_WORLD);
        if (sumErrors > 0)
        	{
          	MrBayesPrint ("%s   Problem with first communication (states).\n", spacer);
             	return ERROR;
            	}

	/* First communication: Send/receive the length of the printString. */
	if (proc_id == 0 || proc_id == maxproc)
		{
		if (maxproc != 0)
				{
				if (proc_id == maxproc)
					{
					if (PrintTree(&sptree, sptree.root, 1, 0, 1) == ERROR)
                                       		{
                                        	printf("Errors in printtree!\n");
                                        	nErrors++;
                                        	}

					/* Find out how large the string is, and send the information to proc_id = 0. */
					ierror = MPI_Send (&printStringSize, 1, MPI_INT, 0, tag, MPI_COMM_WORLD);
					if (ierror != MPI_SUCCESS)
						nErrors++; 
					}
				else
					{
					/* Receive the length of the string from proc_id = procWithChain, and then allocate
					   printString to be that length. */
					ierror = MPI_Recv (&printStringSize, 1, MPI_INT, maxproc, tag, MPI_COMM_WORLD, &status);
					if (ierror != MPI_SUCCESS)
						{
						MrBayesPrint ("%s   Problem receiving printStringSize from proc_id = %d\n", spacer, maxproc);
						nErrors++;
						}
					printString = (char *)SafeMalloc((size_t) (printStringSize * sizeof(char)));
					if (!printString)
						{
						MrBayesPrint ("%s   Problem allocating printString (%d)\n", spacer, printStringSize * sizeof(char));
						nErrors++;
						}
					strcpy (printString, ""); 
					}
				}
		}
	MPI_Allreduce (&nErrors, &sumErrors, 1, MPI_INT, MPI_SUM, MPI_COMM_WORLD);
	if (sumErrors > 0)
		{
		MrBayesPrint ("%s   Problem with first communication (states).\n", spacer);
		return ERROR;
		}

	/* Second communication: Send/receive the printString. */
	if (proc_id == 0 || proc_id == maxproc)
		{
		if (maxproc != 0)
			{					
				if (proc_id == maxproc)
					{
					/* Send the printString to proc_id = 0. After we send the string to proc_id = 0, we can
					   free it. */
					ierror = MPI_Send (&printString[0], printStringSize, MPI_CHAR, 0, tag, MPI_COMM_WORLD);
					if (ierror != MPI_SUCCESS)
						nErrors++;
					free(printString);
					}
				else
					{
					/* Receive the printString from proc_id = maxproc. */
					ierror = MPI_Recv (&printString[0], printStringSize, MPI_CHAR, maxproc, tag, MPI_COMM_WORLD, &status);
					if (ierror != MPI_SUCCESS)
						{
						MrBayesPrint ("%s   Problem receiving printString from proc_id = %d\n", spacer, maxproc);
						nErrors++;
						}
					}
			}
		else	{
				if (PrintTree(&sptree, sptree.root, 1, 0, 1) == ERROR)
        		                {
                        	        printf("Errors in printtree!\n");
                                	nErrors++;
	                        	}
			}
		}
	MPI_Allreduce (&nErrors, &sumErrors, 1, MPI_INT, MPI_SUM, MPI_COMM_WORLD);
	if (sumErrors > 0)
		{
		MrBayesPrint ("%s   Problem with second communication (states).\n", spacer);
		return ERROR;
		}

	/*print to file*/
	if (proc_id == 0)
	{
	       	if (addend == 0)
	        {
        	        if(round == 1)
                	{
   	             		gettimeofday(&tv, NULL);
        	        	curtime = tv.tv_sec;
                		strftime(buffer,30,"%T on %m-%d-%Y",localtime(&curtime));

   		             	fprintf(outfile, "#Nexus\n[This mpest analysis was conducted at local time %s with seed = %ld]\n\n", buffer,seed);
                		fprintf(outfile, "Begin trees;\n");
                	}

                	fprintf(outfile, "\ttree %d [%2.3f] = %s", round, maxloglike, printString);
                	free (printString);
        	}
        	else
        	{
                	fprintf(outfile, "\ttree mpest [%2.3f] = %s", maxloglike, printString);
			fprintf(outfile, "end;\n");
                	free (printString);
        	}
        }

#       else
	/*print to screen*/
        printf("\tround %d \t\tloglike %f \t....completed....\n",round, curLn);

	/*print to file*/
	if (addend == 0)
	{
		if(round == 1)
    		{
				gettimeofday(&tv, NULL);
				curtime = tv.tv_sec;
				strftime(buffer,30,"%T on %m-%d-%Y",localtime(&curtime));

				fprintf(outfile, "#Nexus\n[This mpest analysis was conducted at local time %s with seed = %ld.]\n", buffer, seed);		
				fprintf(outfile, "Begin trees;\n  translate\n");
				for (i=0; i<sptree.ntaxa-1; i++) {
					fprintf(outfile,"\t%d %s,\n", i+1, sptree.nodes[i].taxaname);
				}
				fprintf(outfile,"\t%d %s;\n", sptree.ntaxa, sptree.nodes[sptree.ntaxa-1].taxaname);
       		}

     		if (PrintTree(&sptree, sptree.root, 1, 0, 1) == ERROR)
     		{
				printf("Errors in printtree!\n");
                return (ERROR);
       		}
     		fprintf(outfile, "  tree %d [%2.3f] = %s", round, curLn, printString);
      		free (printString);
	}
	else
	{
		if (PrintTree(&sptree, sptree.root, 1, 0, 1) == ERROR)
		{
			printf("Errors in printtree!\n");
			return (ERROR);
		}
		fprintf(outfile, "  tree mpest [%2.3f] = %s", curLn, printString);
		fprintf(outfile, "end;\n\n");
		free (printString);
		
		/*fprintf(outfile, "Begin trees;\n");
		if (PrintPhylipTree(&sptree, sptree.root, 1, 0, 1) == ERROR)
		{
			printf("Errors in printphyliptree!\n");
			return (ERROR);
		}
		fprintf(outfile, "  tree mpest [%2.3f] = %s", curLn, printString);
		fprintf(outfile, "end;\n");
		free (printString);*/

	}

#       endif

	return (NO_ERROR);
}

void printSptree (void)
{
	int i;

	PrintTree(&sptree, sptree.root, 1, 0, 1);
	free (printString);

	for(i=0;i<2*sptree.ntaxa-1;i++)
		printf("node %d %d %d %d\n",i,sptree.nodes[i].father,sptree.nodes[i].sons[0],sptree.nodes[i].sons[1]);
}

int MoveNode (Tree *tree)
{
	int inode, jnode, father, grandfather, nodes[5], nnodes=0, son;
	double rand = rndu();
	double p[2]={0.4,0.8};

	if(usertree==2) {p[0] = 0.0; p[1]=0.0;}

	if(rand < p[0])
	{
	   do{
		do{
			inode = (int)(rndu() * (2*tree->ntaxa-1));
		}while (inode == tree->root || inode == tree->nodes[tree->root].sons[0] || inode == tree->nodes[tree->root].sons[1]);

		father = tree->nodes[inode].father;
		grandfather = tree->nodes[father].father;

		if(tree->nodes[father].sons[0] == inode)
			son = tree->nodes[father].sons[1];
		else
			son = tree->nodes[father].sons[0];

		if(son >= tree->ntaxa)
		{
			nodes[nnodes++] = tree->nodes[son].sons[0];
			nodes[nnodes++] = tree->nodes[son].sons[1];
		}		
			
		if(tree->nodes[grandfather].sons[0] == father)
			nodes[nnodes++] = son = tree->nodes[grandfather].sons[1];
		else
			nodes[nnodes++] = son = tree->nodes[grandfather].sons[0];
	

		if(son >= tree->ntaxa)
		{
			nodes[nnodes++] = tree->nodes[son].sons[0];
			nodes[nnodes++] = tree->nodes[son].sons[1];
		}
	    }while(nnodes == 0);

		jnode = nodes[(int)(rndu() * nnodes)];
	
		swapNodes (tree, inode, jnode);
	}
	else if (rand < p[1])
	{
		do{
			inode = rndu() * tree->ntaxa;
		}while (tree->nodes[inode].father == tree->root);

		do{
			jnode = rndu() * tree->ntaxa;
		}while (tree->nodes[jnode].father == tree->root || jnode == inode);
		rearrangeNodes (tree, inode, jnode);
	}
	else
		MoveBrlens (tree);

	return (NO_ERROR);
}

void MoveBrlens (Tree *tree)
{
	int inode;
	double brlens, window = 0.1;

	do{
		inode = tree->ntaxa + (int)(rndu() * (tree->ntaxa-1));
	}while (inode == tree->root);

	brlens = tree->nodes[inode].brlens;
	brlens = brlens + (rndu()-0.5) * window;
	if(brlens < 0)
		brlens = 0.000001;
	if(brlens > 7.0)
		brlens = 7.0;
	tree->nodes[inode].brlens = brlens;
}

void copyTree(Tree *from, Tree *to)
{
   	int   i ;
 
  	/*copy the species tree nodes*/
  	to->root = from->root;
	to->ntaxa = from->ntaxa;
  	for(i=0; i<(2*from->ntaxa-1); i++)
  	{
    		to->nodes[i].nson = from->nodes[i].nson;
    		to->nodes[i].sons[0] = from->nodes[i].sons[0];
    		to->nodes[i].sons[1] = from->nodes[i].sons[1];
    		to->nodes[i].father = from->nodes[i].father;
    		to->nodes[i].brlens = from->nodes[i].brlens;
  	}
}

int findOutgroup (Tree *tree)
{
	int outgroup;

	if(tree->nodes[tree->root].sons[0] < tree->ntaxa)
		outgroup = tree->nodes[tree->root].sons[0];
	else
		outgroup = tree->nodes[tree->root].sons[1];

	return (outgroup);
}

int rearrangeNodes (Tree *tree, int fromnode, int tonode)
{
	/*delete fromnode*/
	if(deleteNode(tree, fromnode) == ERROR)
	{
		printf("Errors in deleteNode\n");
		return (ERROR);
	}
	if(addNode(tree, fromnode, tree->nodes[fromnode].father, tonode) == ERROR)
	{
		printf("Errors in addNode\n");
		return (ERROR);
	}
	return (NO_ERROR);
}
int deleteNode (Tree *tree, int inode)
{
	int father, son, grandfather;
	
	if(inode == tree->root)
		return (ERROR);
	father = tree->nodes[inode].father;
	if(tree->nodes[father].sons[0] == inode)
		son = tree->nodes[father].sons[1];
	else
		son = tree->nodes[father].sons[0];
	if(father == tree->root)
	{
		tree->root = son;
	}
	else
	{
		grandfather = tree->nodes[father].father;
		if(tree->nodes[grandfather].sons[0] == father)
			tree->nodes[grandfather].sons[0] = son;
		else
			tree->nodes[grandfather].sons[1] = son;
		tree->nodes[son].father = grandfather;
	}
	return (NO_ERROR);
}
int addNode (Tree *tree, int fromnode, int fromfather, int tonode)
{
	int tofather;
	if(fromfather == -1)
	{
		printf("Errors for fromfather\n");
		return (ERROR);
	}
	tofather = tree->nodes[tonode].father;
	if(tree->nodes[tofather].sons[0] == tonode)
		tree->nodes[tofather].sons[0] = fromfather;
	else
		tree->nodes[tofather].sons[1] = fromfather;
	tree->nodes[fromfather].father = tofather;
	tree->nodes[tonode].father = fromfather;
	if(tree->nodes[fromfather].sons[0] == fromnode)
		tree->nodes[fromfather].sons[1] = tonode;
	else
		tree->nodes[fromfather].sons[0] = tonode;
	return (NO_ERROR);
}

int swapNodes (Tree *tree, int inode, int jnode)
{
	int ifather, jfather;
	ifather = tree->nodes[inode].father;
	jfather = tree->nodes[jnode].father;
	tree->nodes[inode].father = jfather;
	tree->nodes[jnode].father = ifather;
	if(tree->nodes[ifather].sons[0] == inode) 
		tree->nodes[ifather].sons[0] = jnode;
	else
		tree->nodes[ifather].sons[1] = jnode;
	if(tree->nodes[jfather].sons[0] == jnode)
		tree->nodes[jfather].sons[0] = inode;
	else
		tree->nodes[jfather].sons[1] = inode;
	return (NO_ERROR);
}
int maximizeaBrlen (int **triple, Tree *tree)
{
	return (NO_ERROR);
}
void randomVector (int *array, int number)
{
	int i, n, m;
	for(i=0; i<number; i++)
	{
		array[i] = i;
	}
	for(i=0; i<number; i++)
	{
		n = rndu() * (number - i);
		m = array[number-i-1];
		array[number-i-1] = array[n];
		array[n] = m;		
	}
}
void randomTree(Tree *tree)
{
	int i, outgroup, array[NTAXA];
	double brlens = 7.0;
	outgroup = gtree[0].nodes[findOutgroup (&gtree[0])].namenumber;
	randomVector (array, tree->ntaxa);
	tree->nodes[array[0]].nson = 0;
	tree->nodes[array[0]].sons[0] = -1;
	tree->nodes[array[0]].sons[1] = -1;
	tree->nodes[array[0]].father = tree->ntaxa;
	tree->nodes[tree->ntaxa].sons[0] = array[0];
	tree->nodes[array[0]].brlens = brlens;
	
	tree->nodes[array[1]].nson = 0;
	tree->nodes[array[1]].sons[0] = -1;
	tree->nodes[array[1]].sons[1] = -1;
	tree->nodes[array[1]].father = tree->ntaxa;
	tree->nodes[tree->ntaxa].sons[1] = array[1];
	tree->nodes[array[1]].brlens = brlens;
	
	tree->nodes[tree->ntaxa].father = tree->ntaxa + 1;
	tree->nodes[tree->ntaxa].brlens = brlens;
	tree->nodes[tree->ntaxa].nson = 2;
	
	tree->nodes[2*tree->ntaxa-2].nson = 2;
	tree->nodes[2*tree->ntaxa-2].sons[1] = 2*tree->ntaxa-3;
	tree->nodes[2*tree->ntaxa-2].father = -1;
	tree->nodes[2*tree->ntaxa-2].brlens = 0;
	
	for(i=2; i<tree->ntaxa; i++)
	{
		tree->nodes[array[i]].nson = 0;
		tree->nodes[array[i]].sons[0] = -1;
		tree->nodes[array[i]].sons[1] = -1;
		tree->nodes[array[i]].father = tree->ntaxa+i-1;
		tree->nodes[tree->ntaxa+i-1].sons[0] = array[i];
		tree->nodes[array[i]].brlens = brlens;
	}

	for(i=tree->ntaxa+1; i<2*tree->ntaxa-2; i++)
	{
		tree->nodes[i].nson = 2;
		tree->nodes[i].sons[1] = i-1;
		tree->nodes[i].father = i+1;
		tree->nodes[i].brlens = brlens;
	}
	tree->root = 2*tree->ntaxa-2;

	if(outgroup != array[tree->ntaxa - 1])
		swapNodes (tree, outgroup, array[tree->ntaxa-1]);

	/*for(i=0; i<tree->ntaxa; i++)
	{
		printf("%d ", array[i]);
	}
	printSptree();
	printf("outgroup %d\n",outgroup);
	exit(-1);*/
}

/**
 * Calculates the quartet, more importantly generates the quartet matrix
 * By Tim Shaw
 */
int quartet (Tree *tree, int ntrees)
{
	int i, j, k;
	long int sum0=0, nquartets = NChoose4Count;

	for(i=0; i<ntrees; i++)
	{
		if(quartetInatree (quartetmatrix, quartetmatrixOnce, &tree[i]) == ERROR)
		{
			printf("Errors in the quartetInatree function\n");
			return (ERROR);
		}
	}

	for(j=0;j<nquartets;j++)
	{
		for(k=0;k<3;k++)
			quartetmatrix[j][3] += quartetmatrix[j][k];
		    sum0 += quartetmatrix[j][3];
	}
	return (NO_ERROR);
}

/** tripples */
int triples (Tree *tree, int ntrees)
{
	int i, j, k;
	long int sum0=0, ntriples = sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)/6;

	for(i=0; i<ntrees; i++)
	{
		if(triplesInatree (triplematrix, &tree[i]) == ERROR)
		{
			printf("Errors in the tripleInatree function\n");
			return (ERROR);
		}
	}

	for(j=0;j<ntriples;j++)
	{
		for(k=0;k<3;k++)
			triplematrix[j][3] += triplematrix[j][k];
		    sum0 += triplematrix[j][3];
	}
/*	for(j=0; j<nGene; j++)
		sum1 += (tree[j].ntaxa*(tree[j].ntaxa-1)*(tree[j].ntaxa-2)/6);

	if(sum0 != sum1)
	{
		printf("the number of triples %ld != %ld\n",sum0,sum1);
		return (ERROR);
	}
*/
	return (NO_ERROR);
}

void PrintHeader (void)
{
#		if !defined (MPI_ENABLED)
		MrBayesPrint ("\n\n");
#		endif
		MrBayesPrint ("%s            Maximum Pseudo-likelihood Estimation of Species Trees (MPEST)  \n\n",spacer);
#		if defined (MPI_ENABLED)
		MrBayesPrint ("                             (Parallel version)\n");
		MrBayesPrint ("                         (%d processors available)\n\n", num_procs);
#		endif
		srand ((unsigned int)time(NULL));
			MrBayesPrint ("%s                                   by\n\n",spacer);
			MrBayesPrint ("%s                                Liang Liu\n\n",spacer);
			MrBayesPrint ("%s              Department of Organismic and Evolutionary Biology\n",spacer);
			MrBayesPrint ("%s                          Harvard University\n",spacer);
			MrBayesPrint ("%s                         lliu@oeb.harvard.edu\n\n",spacer);
			MrBayesPrint ("%s                         Modified by Timothy Shaw\n\n",spacer);					    MrBayesPrint ("%s              Institute of Bioinformatics\n",spacer);
			MrBayesPrint ("%s                          University of Georgia\n",spacer);
			MrBayesPrint ("%s                         gatechatl@gmail.com\n\n",spacer);
		        MrBayesPrint ("%s               Distributed under the GNU General Public License\n\n",spacer);	
}

/** tim added function */
int quartetInatree (int **quartet, int **quartetOnce, Tree *tree)
{
	int i, j, k, son0, son1;
	int offsprings0[NTAXA * 2], offsprings1[NTAXA * 2], n0, n1, n2, n3;
	long int location[2];
 	
	for (i = 0; i < NChoose4Count; i++) {
		for (j = 0; j < 4; j++) {
			quartetOnce[i][j] = 0;
		}
	}

	//printf("ntaxa: %d\n", tree->ntaxa); 
	for(i=tree->ntaxa; i<2*tree->ntaxa-1; i++)
	{

		if(i == tree->root)
			continue;
		else {

		//printf("Ith iterations: %d\n", i);	
		n0 = n1 = n2 = n3 = 0;
		for(k = 0; k < tree->ntaxa; k++)
		{
			offsprings0[k] = 0;
			offsprings1[k] = 0;
		}

		// grab all the
		findOffsprings(offsprings0, tree, i);
		for (j = 0; j < tree-> ntaxa; j++) {
                        if (offsprings0[j] == 0) {
  			    offsprings1[j] = 1;
			    //printf("Found\n");
			}
			//printf("offsprings0[%d]=%d\n", j, offsprings0[j]);
			//printf("offsprings1[%d]=%d\n", j, offsprings1[j]);			
		}

		// replace offsprings0 with node name number;
		for(j=0; j<tree->ntaxa; j++) {
			if (offsprings0[j] == 1) {
				offsprings0[n0++] = tree->nodes[j].namenumber;
			}
			if (offsprings1[j] == 1) {
				offsprings1[n1++] = tree->nodes[j].namenumber;
			}			
		}
		//printf("n0: %d n1: %d\n", n0, n1);	
		if (n0 >= 2 && n1 >= 2) {
                
		int node1, node2, node3, node4;
		// iterate through all the possible offsprings0 n0 choose 2
		int N1 = n0;
		int M = 2;
		int x1, y1, z1, p1[N1+2], b1[N1];

		// iterate through all the possible offsprings1 n1 choose 2
		int N2 = n1;
		int x2, y2, z2, p2[N2+2], b2[N2];		

		int l, m;

		if (n0 >= 2) {		
			inittwiddle(M, N1, p1);
	
			for (l = 0; l != N1 - M; l++) {
				b1[l] = 0;
			}
			while (l != N1) {
				b1[l++] = 1;
			}			
					
			// grab node 1 and node2
			node1 = -1; node2 = -1;
			for (l = 0; l < n0; l++) {
				if (b1[l] == 1) {
					if (node1 == -1) {
						node1 = offsprings0[l];
					} else {
						node2 = offsprings0[l];
					}
				}
			}
	
	
			if (n1 >= 2) {
				inittwiddle(M, N2, p2);		
				for (m = 0; m != N2 - M; m++) {
					b2[m] = 0;
				}
				while (m != N2) {
					b2[m++] = 1;
				}	
		
	
				// grab node3 and node4		
				node3 = -1; node4 = -1;		
				for (m = 0; m < n1; m++) {
					if (b2[m] == 1) {
						if (node3 == -1) {
							node3 = offsprings1[m];
						} else {
							node4 = offsprings1[m];
						}
					}
				}
			
				findQuartet (node1, node2, node3, node4, location); // test those three locations
				//printf("nodes %d %d %d %d location %ld %ld\n", node1, node2, node3, node4, location[0], location[1]);
				if (quartetOnce[location[0]][location[1]] == 0) {
					quartet[location[0]][location[1]]++;		

					quartetOnce[location[0]][location[1]] = 1;
				}
	
				while(!twiddle(&x2, &y2, &z2, p2)) {
					b2[x2] = 1;
					b2[y2] = 0;
		
					node3 = -1; node4 = -1;
					for (m = 0; m < n1; m++) {
						if (b2[m] == 1) {
							if (node3 == -1) {
								node3 = offsprings1[m];
							} else {
								node4 = offsprings1[m];
							}
						}
					}	
		
					findQuartet (node1, node2, node3, node4, location); // test those three locations
					//printf("nodes %d %d %d %d location %ld %ld\n", node1, node2, node3, node4, location[0], location[1]);
					if (quartetOnce[location[0]][location[1]] == 0) {
						quartet[location[0]][location[1]]++;		

						quartetOnce[location[0]][location[1]] = 1;
					}					
					//quartet[location[0]][location[1]]++;		
				}

			} 

			// continue n0 choose 2
			while(!twiddle(&x1, &y1, &z1, p1)) {
				b1[x1] = 1;
				b1[y1] = 0;
	
				node1 = -1; node2 = -1;
				for (l = 0; l < n0; l++) {
					if (b1[l] == 1) {
						if (node1 == -1) {
							node1 = offsprings0[l];
						} else {
							node2 = offsprings0[l];
						}
					}
				}
	
				if (n1 >= 2) {
					// generate all possible n1 choose 2
					inittwiddle(M, N2, p2);
					for (m = 0; m != N2 - M; m++) {
						b2[m] = 0;
					}
					while (m != N2) {
						b2[m++] = 1;
					}	
		
					// grab node3 and node4		
					node3 = -1; node4 = -1;
					for (m = 0; m < n1; m++) {
						if (b2[m] == 1) {
							if (node3 == -1) {
								node3 = offsprings1[m];
							} else {
								node4 = offsprings1[m];
							}
						}
					}
			
					findQuartet (node1, node2, node3, node4, location); // test those three locations
					//printf("nodes %d %d %d %d location %ld %ld\n", node1, node2, node3, node4, location[0], location[1]);
					if (quartetOnce[location[0]][location[1]] == 0) {
						quartet[location[0]][location[1]]++;		

						quartetOnce[location[0]][location[1]] = 1;
					}
					//quartet[location[0]][location[1]]++;		
			
					while(!twiddle(&x2, &y2, &z2, p2)) {
						b2[x2] = 1;
						b2[y2] = 0;
			
						node3 = -1; node4 = -1;
						for (m = 0; m < n1; m++) {
							if (b2[m] == 1) {
								if (node3 == -1) {
									node3 = offsprings1[m];
								} else {
									node4 = offsprings1[m];
								}
							}
						}	
			
						findQuartet (node1, node2, node3, node4, location); // test those three locations
						//printf("nodes %d %d %d %d location %ld %ld\n", node1, node2, node3, node4, location[0], location[1]);
						if (quartetOnce[location[0]][location[1]] == 0) {
							quartet[location[0]][location[1]]++;		

							quartetOnce[location[0]][location[1]] = 1;
						}
						//quartet[location[0]][location[1]]++;		
					}
		
				} 
			} // end while loop
		

		} /*else { // if n0 <= 2
			node1 = offsprings0[0];
			node2 = offsprings0[1];
			if (n1 > 2) {
				// generate all possible n1 choose 2
				inittwiddle(M, N2, p2);
				for (m = 0; m != N2 - M; m++) {
					b2[m] = 0;
				}
				while (m != N2) {
					b2[m++] = 1;
				}	
	
				// grab node3 and node4		
				node3 = -1; node4 = -1;
				for (m = 0; m < n1; m++) {
					if (b2[m] == 1) {
						if (node3 == -1) {
							node3 = offsprings1[m];
						} else {
							node4 = offsprings1[m];
						}
					}
				}
		
				findQuartet (node1, node2, node3, node4, location); // test those three locations
				//printf("nodes %d %d %d %d location %ld %ld\n", node1, node2, node3, node4, location[0], location[1]);
				quartet[location[0]][location[1]]++;		
		
				while(!twiddle(&x2, &y2, &z2, p2)) {
					b2[x2] = 1;
					b2[y2] = 0;
		
					node3 = -1; node4 = -1;
					for (m = 0; m < n1; m++) {
						if (b2[m] == 1) {
							if (node3 == -1) {
								node3 = offsprings1[m];
							} else {
								node4 = offsprings1[m];
							}
						}
					}	
		
					findQuartet (node1, node2, node3, node4, location); // test those three locations
					//printf("nodes %d %d %d %d location %ld %ld\n", node1, node2, node3, node4, location[0], location[1]);
					quartet[location[0]][location[1]]++;		
				} // end twiddle				
			} 
		}*/



		if(DEBUG)
		{
			printf("\n");
			for(j=0; j<tree->ntaxa; j++)
				printf("off %d %d %d %s %d %s\n",son0, son1, offsprings0[j],tree->nodes[offsprings0[j]].taxaname,offsprings1[j],tree->nodes[offsprings1[j]].taxaname);
		}

		}
		}
	}
	return(NO_ERROR);
}

int triplesInatree (int **triple, Tree *tree)
{
	int i, j, k, w, son0, son1;
	int offsprings0[NTAXA], offsprings1[NTAXA], offsprings2[NTAXA], n0, n1, n2;
	long int location[2];
 	
	
	for(i=tree->ntaxa; i<2*tree->ntaxa-1; i++) // iterating through the internal nodes...
	{
		if(i == tree->root)
			continue;
		else
		{
			n0 = n1 = n2 = 0;
			for(j=0; j<tree->ntaxa; j++)
			{
				offsprings0[j] = 0; // put everything as 0
				offsprings1[j] = 0; // put everything as 0
			}

			son0 = tree->nodes[i].sons[0]; // ith node should be an internal node... sons[0] is left child sons[1] is right child.
			son1 = tree->nodes[i].sons[1];
			findOffsprings (offsprings0, tree, son0); // recursively go through the tree to find all the children inside the node
			findOffsprings (offsprings1, tree, son1);

			if(DEBUG)
			{
				printf("\n\n");
				for(j=0; j<tree->ntaxa; j++)
					printf("off %d %d %d %s %d %s\n",son0, son1, offsprings0[j],tree->nodes[offsprings0[j]].taxaname,offsprings1[j],tree->nodes[offsprings1[j]].taxaname);
			}
			

			for(j=0; j<tree->ntaxa; j++) // examines which ntaxa is within each offsprings tree
			{
				if(offsprings0[j] == 0 && offsprings1[j] == 0) {
					offsprings2[n2++] = tree->nodes[j].namenumber; // offspring 2 stores all the non leaf edges
					//printf("NameNumber for Offspring2: %d\n", tree->nodes[j].namenumber);
				}else if (offsprings0[j] == 1 && offsprings1[j] == 0) 
					offsprings0[n0++] = tree->nodes[j].namenumber;
				else if (offsprings0[j] == 0 && offsprings1[j] == 1)
					offsprings1[n1++] = tree->nodes[j].namenumber;
				else
					return (ERROR);
			}

			//offsprings3 = offsprings2;

			for(j=0; j<n0; j++)
			{
				for(k=0; k<n1; k++)
				{
					if(offsprings0[j] == offsprings1[k]) // if they are both equal it should only mean that they are both 0s
						continue;

					for(w=0; w<n2; w++)
					{
//						for (v = 0; v < n2; v++) {								
//							if(offsprings0[j] == offsprings2[w] || offsprings1[k] == offsprings2[w] || offsprings0[j] == offsprings3[v] || offsprings1[k] == offsprings3[v] || offsprings2[w] == offsprings3[v])
							if (offsprings0[j] == offsprings2[w] || offsprings1[k] == offsprings2[w])							
								continue;
							findTriple (offsprings0[j], offsprings1[k], offsprings2[w], location); // test those three locations
							triple[location[0]][location[1]]++;

							if(DEBUG)
							{
								printf("nodes %d %d %d location %ld %ld\n",offsprings0[j], offsprings1[k], offsprings2[w],location[0],location[1]);
							}
//						}
					}
				}
			}
					
		}
	}
	return(NO_ERROR);
}


/*
 * Check if node is uniq to the four nodes
 *
 */
int checkIfUniq(int node1, int node2, int node3, int node4, int internalnode, Tree *tree) {
	int count = 0;
	int totalNodes = 2 * tree -> ntaxa - 1;
	int offsprings1[totalNodes];
	int i;	
	for (i = 0; i < tree->ntaxa; i++) {
		offsprings1[i] = 0;
	}

	findOffsprings(offsprings1, tree, internalnode); // recursively go through the tree to find all the children inside the node	

	for (i = 0; i < tree->ntaxa; i++) {
		if (offsprings1[i] == 1 && i == node1) {
			count++;
		}
		if (offsprings1[i] == 1 && i == node2) {
			count++;
		}
		if (offsprings1[i] == 1 && i == node3) {
			count++;
		}
		if (offsprings1[i] == 1 && i == node4) {
			count++;
		}		
	}
	//printf("count: %d\n", count);
	if (count > 2) {
		return 0;
	} else if (count == 2) {
		return 1;
	} else {
		return -1;
	}
}
/*
 * Identify Lowest Common intersecting node
 *
 */
int intersect2(int node1, int node2, Tree *tree) {

	int totalNodes = 2 * tree->ntaxa - 1;
	//printf("die already?\n");
	int offsprings1[totalNodes], offsprings2[totalNodes], i, j, n1, n2;
	int findintersect = 1;    

	for (i = 0; i < 2*tree->ntaxa-1; i++) {
		offsprings1[i] = 0;
		offsprings2[i] = 0;
	}
	
	n1 = 0;
	n2 = 0;
	int tempnode1 = node1;
	int tempnode2 = node2;
	
	while (findintersect == 1) {


		//int fathernode1 = tree->nodes[tempnode1].father;
		offsprings1[n1] = tree->nodes[tempnode1].father;
		
		//int fathernode2 = tree->nodes[tempnode2].father;
		offsprings2[n2] = tree->nodes[tempnode2].father;
		
		
		if (tree->nodes[tempnode1].father != -1)		
			tempnode1 = tree->nodes[tempnode1].father;
		if (tree->nodes[tempnode2].father != -1)
			tempnode2 = tree->nodes[tempnode2].father;
		
		//printf("fathernode1: %d fathernode2: %d\n", tempnode1, tempnode2);
		n1++;
		n2++;
		for (i = 0; i < n1; i++) {
			for (j = 0; j < n2;j++) {
				if (offsprings1[i] != 0 && offsprings2[j] != 0) {
					if (offsprings1[i] == offsprings2[j]) {
						return offsprings1[i];
					}
				}
			}
		}
		if (tree->nodes[tempnode1].father == -1 && tree->nodes[tempnode2].father == -1) {
			findintersect = 0;
		}
	}
	return -1;
}

/*
 * Identify Lowest Common intersecting node
 *
 */
int intersect3(int node1, int node2, int node3, Tree *tree) {
	int totalNodes = 2 * tree -> ntaxa - 1;
	int offsprings1[totalNodes], offsprings2[totalNodes], offsprings3[totalNodes], i, j, k, n1, n2, n3, findintersect = 1;    
	for (i = 0; i < 2*tree->ntaxa-1; i++) {
		offsprings1[i] = 0;
		offsprings2[i] = 0;
		offsprings3[i] = 0;		
	}
	n1 = 0;
	n2 = 0;
	n3 = 0;	

	int tempnode1 = node1;
	int tempnode2 = node2;
	int tempnode3 = node3;	
	while (findintersect == 1) {

		//int fathernode1 = tree->nodes[tempnode1].father;
		offsprings1[n1] = tree->nodes[tempnode1].father;
		
		//int fathernode2 = tree->nodes[tempnode2].father;
		offsprings2[n2] = tree->nodes[tempnode2].father;

		//int fathernode3 = tree->nodes[tempnode3].father;
		offsprings3[n3] = tree->nodes[tempnode3].father;		
		

		if (tree->nodes[tempnode1].father != -1)		
			tempnode1 = tree->nodes[tempnode1].father;
		if (tree->nodes[tempnode2].father != -1)
			tempnode2 = tree->nodes[tempnode2].father;
		if (tree->nodes[tempnode3].father != -1)
			tempnode3 = tree->nodes[tempnode3].father;		

		n1++;
		n2++;
		n3++;		
		for (i = 0; i < n1; i++) {
			for (j = 0; j < n2;j++) {
				for (k = 0; k < n3; k++) {				
					//printf("i %d j %d k %d 1 %d 2 %d 3 %d\n", i, j, k, offsprings1[i], offsprings2[j], offsprings3[k]);					
					if (offsprings1[i] != 0 && offsprings2[j] != 0 && offsprings3[k] != 0) {
						if (offsprings1[i] == offsprings2[j] && offsprings1[i] == offsprings3[k]) {
							return offsprings1[i];
						}
					}
				}
			}
		}
		if (tree->nodes[tempnode1].father == -1 && tree->nodes[tempnode2].father == -1 && tree->nodes[tempnode3].father == -1) {
			findintersect = 0;
		}
	}
	return -1;
}

/*
 * Identify Lowest Common intersecting node
 *
 */
int intersect4(int node1, int node2, int node3, int node4, Tree *tree) {
	int totalNodes = 2 * tree -> ntaxa - 1;
	int offsprings1[totalNodes], offsprings2[totalNodes], offsprings3[totalNodes], offsprings4[totalNodes], i, j, k, l, n1, n2, n3, n4, findintersect = 1;    
	for (i = 0; i < 2*tree->ntaxa-1; i++) {
		offsprings1[i] = 0;
		offsprings2[i] = 0;
		offsprings3[i] = 0;		
		offsprings4[i] = 0;			
	}
	n1 = 0;
	n2 = 0;
	n3 = 0;	
	n4 = 0;

	int tempnode1 = node1;
	int tempnode2 = node2;
	int tempnode3 = node3;	
	int tempnode4 = node4;		
	while (findintersect == 1) {
		
		int fathernode1 = tree->nodes[tempnode1].father;
		offsprings1[n1] = fathernode1;
		
		int fathernode2 = tree->nodes[tempnode2].father;
		offsprings2[n2] = fathernode2;

		int fathernode3 = tree->nodes[tempnode3].father;
		offsprings3[n3] = fathernode3;		

		int fathernode4 = tree->nodes[tempnode4].father;
		offsprings4[n4] = fathernode4;				

		if (fathernode1 != -1)		
			tempnode1 = fathernode1;
		if (fathernode2 != -1)
			tempnode2 = fathernode2;
		if (fathernode3 != -1)
			tempnode3 = fathernode3;		
		if (fathernode4 != -1)		
			tempnode4 = fathernode4;			

		n1++;
		n2++;
		n3++;		
		n4++;				
		for (i = 0; i < n1; i++) {
			for (j = 0; j < n2; j++) {
				for (k = 0; k < n3; k++) {				
					for (l = 0; l < n4; l++) {
						//printf("i %d j %d k %d l %d 1 %d 2 %d 3 %d 4 %d\n", i, j, k, l, offsprings1[i], offsprings2[j], offsprings3[k], offsprings4[l]);
						if (offsprings1[i] != 0 && offsprings2[j] != 0 && offsprings3[k] != 0 && offsprings4[l] != 0) {
							if (offsprings1[i] == offsprings2[j] && offsprings1[i] == offsprings3[k] && offsprings1[i] == offsprings4[l]) {
								return offsprings1[i];
							}
						}
					}
				}
			}
		}
		if (tree->nodes[tempnode1].father == -1 && tree->nodes[tempnode2].father == -1 && tree->nodes[tempnode3].father == -1 && tree->nodes[tempnode4].father == -1) {
			findintersect = 0;
		}
	}
	return -1;
}
/*
 * Find the branch length between four species
 *
 */
double findBranchLength(int node1, int node2, int node3, int node4, Tree *tree, long int *location) {
	int totalNodes = 2 * tree -> ntaxa - 1;
	int offsprings1[totalNodes], offsprings2[totalNodes], offsprings3[totalNodes], offsprings4[totalNodes], overlapNode1Node2[totalNodes], overlapNode3Node4[totalNodes], i;
	for (i = 0; i < 2 * tree->ntaxa-1; i++) {
             offsprings1[i] = 0;
	     offsprings2[i] = 0;
	     offsprings3[i] = 0;
	     offsprings4[i] = 0;
	     overlapNode1Node2[i] = 0;
	     overlapNode3Node4[i] = 0;
	}
	labelAllFather(offsprings1, tree, node1, 0); // label all the father nodes for node1
	labelAllFather(offsprings2, tree, node2, 0); // label all the father nodes for node2
	labelAllFather(offsprings3, tree, node3, 0); // label all the father nodes for node3
	labelAllFather(offsprings4, tree, node4, 0); // label all the father nodes for node4

	//int commonNode = intersect4(node1, node2, node3, node4, tree);

	int inode12 = intersect2(node1, node2, tree);
	int inode13 = intersect2(node1, node3, tree);	
	int inode14 = intersect2(node1, node4, tree);
	int inode34 = intersect2(node3, node4, tree);
	int inode24 = intersect2(node2, node4, tree);	
	int inode23 = intersect2(node2, node3, tree);	

	if (DEBUG) {
	printf("offsprings1 for node: %d\n", node1);
	for (i = 0; i < 2 * tree->ntaxa-1; i++) {
		printf("%d ", offsprings1[i]);
        }	
	printf("\n");
	printf("offsprings2 for node: %d\n", node2);
	for (i = 0; i < 2 * tree->ntaxa-1; i++) {
		printf("%d ", offsprings2[i]);
	}
	printf("\n");
	printf("offsprings3 for node: %d\n", node3);
	for (i = 0; i < 2 * tree->ntaxa-1; i++) {
		printf("%d ", offsprings3[i]);
	}
	printf("\n");
	printf("offsprings4 for node: %d\n", node4);
	for (i = 0; i < 2 * tree->ntaxa-1; i++) {
		printf("%d ", offsprings4[i]);
	}
	printf("\n");

	printf("Node1: %d Node2: %d Node3 %d Node4 %d\n", node1, node2, node3, node4);
	printf("inode12: %d inode13: %d inode14:  %d inode34: %d inode24: %d inode23: %d\n", inode12, inode13, inode14, inode34, inode24, inode23);

	printf("checkIfUniq inode12 %d\n", checkIfUniq(node1, node2, node3, node4, inode12, tree));
	printf("checkIfUniq inode13 %d\n", checkIfUniq(node1, node2, node3, node4, inode13, tree));
	printf("checkIfUniq inode14 %d\n", checkIfUniq(node1, node2, node3, node4, inode14, tree));
	printf("checkIfUniq inode34 %d\n", checkIfUniq(node1, node2, node3, node4, inode34, tree));
	printf("checkIfUniq inode24 %d\n", checkIfUniq(node1, node2, node3, node4, inode24, tree));
	printf("checkIfUniq inode23 %d\n", checkIfUniq(node1, node2, node3, node4, inode23, tree));
	}
	double brlen = 0.0;

	if (checkIfUniq(node1, node2, node3, node4, inode12, tree) == 1) {
		// node1 and node2 are together
		findQuartet(node1, node2, node3, node4, location); 
		if (checkIfUniq(node1, node2, node3, node4, inode34, tree) == 1) {

			brlen = calcBrLenMerge2(inode12, inode34, tree);

			//brlen = calcBrLen(offsprings1, offsprings2, offsprings3, offsprings4, tree, commonNode);		
		} else {
			int inode123 = intersect3(node1, node2, node3, tree);
			int inode124 = intersect3(node1, node2, node4, tree);			
			//brlen = calcBrLen2(offsprings1, offsprings2, offsprings3, offsprings4, tree, commonNode, inode123, inode124);
			//printf("inode123: %d inode124: %d\n", inode123, inode124);										
			brlen = calcBrLenMerge3(tree, inode12, inode123, inode124);			
			//printf("check here brlen: %4.2f\n", brlen);

			//printf("commonNode %d inode123 %d inode124 %d\n", commonNode, inode123, inode124);
		}
	} else 	if (checkIfUniq(node1, node2, node3, node4, inode34, tree) == 1) {
		// node3 and node4 are together
		findQuartet(node3, node4, node1, node2, location); 
		if (checkIfUniq(node1, node2, node3, node4, inode12, tree) == 1) {
			brlen = calcBrLenMerge2(inode34, inode12, tree);
			//brlen = calcBrLen(offsprings1, offsprings2, offsprings3, offsprings4, tree, commonNode);		
		} else {
			int inode341 = intersect3(node3, node4, node1, tree);
			int inode342 = intersect3(node3, node4 ,node2, tree);			
			//brlen = calcBrLen2(offsprings3, offsprings4, offsprings1, offsprings2, tree, commonNode, inode341, inode342);
			//printf("inode341: %d inode342: %d\n", inode341, inode342);							
			brlen = calcBrLenMerge3(tree, inode34, inode341, inode342);
		}						
		
	} else if (checkIfUniq(node1, node2, node3, node4, inode13, tree) == 1) {
		// node1 and node3 are together
		findQuartet(node1, node3, node2, node4, location); 
		if (checkIfUniq(node1, node2, node3, node4, inode24, tree) == 1) {
			brlen = calcBrLenMerge2(inode13, inode24, tree);			
			//brlen = calcBrLen(offsprings1, offsprings3, offsprings2, offsprings4, tree, commonNode);
		} else {
			int inode132 = intersect3(node1, node3, node2, tree);
			int inode134 = intersect3(node1, node3, node4, tree);			
			//brlen = calcBrLen2(offsprings1, offsprings3, offsprings2, offsprings4, tree, commonNode, inode132, inode134);
			//printf("inode132: %d inode134: %d\n", inode132, inode134);				
			brlen = calcBrLenMerge3(tree, inode13, inode132, inode134);
		}
	} else if (checkIfUniq(node1, node2, node3, node4, inode24, tree) == 1) {
		// node2 and node4 are together
		findQuartet(node2, node4, node1, node3, location); 
		if (checkIfUniq(node1, node2, node3, node4, inode13, tree) == 1) {
			brlen = calcBrLenMerge2(inode24, inode13, tree);					
			//brlen = calcBrLen(offsprings1, offsprings3, offsprings2, offsprings4, tree, commonNode);
		} else {
			int inode241 = intersect3(node2, node4, node1, tree);
			int inode243 = intersect3(node2, node4, node3, tree);			
			//brlen = calcBrLen2(offsprings2, offsprings4, offsprings1, offsprings3, tree, commonNode, inode241, inode243);
			//printf("inode241: %d inode243: %d\n", inode241, inode243);			
			brlen = calcBrLenMerge3(tree, inode24, inode241, inode243);			
		}
	} else if (checkIfUniq(node1, node2, node3, node4, inode14, tree) == 1) {
		// node1 and node4 are together
		findQuartet(node1, node4, node2, node3, location); 
		if (checkIfUniq(node1, node2, node3, node4, inode23, tree) == 1) {
			brlen = calcBrLenMerge2(inode14, inode23, tree);								
			//brlen = calcBrLen(offsprings1, offsprings4, offsprings2, offsprings3, tree, commonNode);
		} else {
			int inode142 = intersect3(node1, node4, node2, tree);			
			int inode143 = intersect3(node1, node4, node3, tree);						
			//brlen = calcBrLen2(offsprings1, offsprings4, offsprings2, offsprings3, tree, commonNode, inode142, inode143);
			//printf("inode142: %d inode143: %d\n", inode142, inode143);
			brlen = calcBrLenMerge3(tree, inode14, inode142, inode143);
		}		
	} else if (checkIfUniq(node1, node2, node3, node4, inode23, tree) == 1) {
		// node1 and node4 are together
		findQuartet(node2, node3, node1, node4, location); 
		if (checkIfUniq(node1, node2, node3, node4, inode14, tree) == 1) {
			brlen = calcBrLenMerge2(inode23, inode14, tree);								
			//brlen = calcBrLen(offsprings2, offsprings3, offsprings1, offsprings4, tree, commonNode);
		} else {
			int inode231 = intersect3(node2, node3, node1, tree);			
			int inode234 = intersect3(node2, node3, node4, tree);						
			//brlen = calcBrLen2(offsprings2, offsprings3, offsprings1, offsprings4, tree, commonNode, inode231, inode234);
			//printf("inode231: %d inode234: %d\n", inode231, inode234);
			brlen = calcBrLenMerge3(tree, inode23, inode231, inode234);
		}		
	} else {
		/*
		 * 	int inode12 = intersect2(node1, node2, tree);
	int inode13 = intersect2(node1, node3, tree);	
	int inode14 = intersect2(node1, node4, tree);
	int inode34 = intersect2(node3, node4, tree);
	int inode24 = intersect2(node2, node4, tree);	
	int inode23 = intersect2(node2, node3, tree);	*/
		
		printf("***************Problem In findBranchLength***************");
	}	
	//printf("finaBranchLength brlen %4.2f\n", brlen);
	return brlen;
}
double calcBrLen(int *offsprings1, int *offsprings2, int *offsprings3, int *offsprings4, Tree *tree, int maxNode) {

	int totalNodes = 2 * tree -> ntaxa - 1;
	int overlapNode1Node2[totalNodes], overlapNode3Node4[totalNodes];
	int i;

	for (i = 0; i < 2 * tree->ntaxa-1; i++) {
	     overlapNode1Node2[i] = 0;
	     overlapNode3Node4[i] = 0;
	}	
	double brlen = 0;
	int keepAdding12 = 1;
	int keepAdding34 = 1;
	mergeOffsprings(offsprings1, offsprings2, overlapNode1Node2, tree);
	mergeOffsprings(offsprings3, offsprings4, overlapNode3Node4, tree);	
	for(i = 0; i < totalNodes; i++) { 
   	     if (containsValue(overlapNode1Node2, i, totalNodes) && i == maxNode) {
			keepAdding12 = 0;
	     }
	     if (containsValue(overlapNode3Node4, i, totalNodes) && i == maxNode) {
			keepAdding34 = 0;
	     }	  	     

	     if (containsValue(overlapNode1Node2, i, totalNodes) && tree-> root != i && keepAdding12 == 1) {
		     
                 brlen += tree->nodes[i].brlens;
		 //printf("calcBrLen overlapNode1Node2: %4.2f", brlen);
	     }

	     if (containsValue(overlapNode3Node4, i, totalNodes) && tree-> root != i && keepAdding34 == 1) {
                 brlen += tree->nodes[i].brlens;
		 //printf("calcBrLen overlapNode3Node4: %4.2f", brlen);		 
	     }	     


	}
	return brlen; 
}
double calcBrLenMerge2(int maxNode12, int maxNode34, Tree *tree) {

	int mergeBoth = intersect2(maxNode12, maxNode34, tree);
	//printf("mergeBoth maxNode12 and maxNode34: %d \n", mergeBoth);				
	int reachEdge = 0;
	double brlen = 0;
	int currentNode = maxNode12;
	while(reachEdge == 0) {
		//printf("currentNode: %d root %d\n", currentNode, tree->root);
		brlen += tree->nodes[currentNode].brlens;
		currentNode = tree->nodes[currentNode].father;
		if (currentNode == mergeBoth || currentNode == mergeBoth) {
			reachEdge = 1;
		}		
	}	
	reachEdge = 0;
	currentNode = maxNode34;
	while(reachEdge == 0) {
		//printf("currentNode: %d root %d\n", currentNode, tree->root);
		brlen += tree->nodes[currentNode].brlens;
		currentNode = tree->nodes[currentNode].father;
		if (currentNode == mergeBoth || currentNode == mergeBoth) {
			reachEdge = 1;
		}		
	}		
	return brlen;
}
double calcBrLenMerge3(Tree *tree, int maxNode12, int maxNode123, int maxNode124) {
	int reachEdge = 0;
	double brlen = 0;
	int currentNode = maxNode12;
	while(reachEdge == 0) {
		//printf("currentNode: %d root %d\n", currentNode, tree->root);
		brlen += tree->nodes[currentNode].brlens;
		currentNode = tree->nodes[currentNode].father;

		if (currentNode == maxNode123 || currentNode == maxNode124) {
			return brlen;
			reachEdge = 1;
		}		
	}
	return brlen;
}


double calcBrLen2(int *offsprings1, int *offsprings2, int *offsprings3, int *offsprings4, Tree *tree, int maxNode, int maxNode13, int maxNode14) {
	//printf("run CalcBrLen2\n");
	int totalNodes = 2 * tree -> ntaxa - 1;
	int overlapNode1Node2[totalNodes], overlapNode1Node3[totalNodes], overlapNode1Node4[totalNodes];	
	int i;
	for (i = 0; i < 2 * tree->ntaxa-1; i++) {
	     overlapNode1Node2[i] = 0;
	     overlapNode1Node3[i] = 0;
	     overlapNode1Node4[i] = 0;	     
	}			
	double brlen = 0;	
	int keepAdding12 = 1;
	int keepAdding = 1;
	mergeOffsprings(offsprings1, offsprings2, overlapNode1Node2, tree);
	mergeOffsprings(offsprings1, offsprings3, overlapNode1Node3, tree);	
	mergeOffsprings(offsprings1, offsprings4, overlapNode1Node4, tree);		
	for(i = tree -> ntaxa ; i < totalNodes; i++) { 
	     //printf("i: %d\n", i);
	     //if (overlapNode1Node2[i] == maxNode) {

	     if (containsValue(overlapNode1Node2, i, totalNodes)  && i == maxNode) {
			keepAdding12 = 0;
	     }
	     //if (overlapNode1Node3[i] == maxNode13) {
	     if (containsValue(overlapNode1Node3, i, totalNodes) && i == maxNode13) {
			keepAdding = 0;
	     }
	     if (containsValue(overlapNode1Node4, i, totalNodes) && i == maxNode14) {
			keepAdding = 0;
	     }	     

	     if (tree->root != i && containsValue(overlapNode1Node2, i, totalNodes) && keepAdding12 == 1 && keepAdding == 1) {		     

                 brlen += tree->nodes[i].brlens;
		 //prev = tree->nodes[i].brlens;
 		 //printf("calcBrLen2 overlapNode1Node2[i]: %d brlen: %4.2f\n", i, tree->nodes[overlapNode1Node2[i]].brlens);
	     }
	     if (tree->root != i && containsValue(overlapNode1Node3, i, totalNodes) && keepAdding12 == 1 && keepAdding == 1) {		     
                 brlen += tree->nodes[i].brlens;
		 //prev = tree->nodes[i].brlens;
 		 //printf("calcBrLen2 overlapNode1Node3[i]: %d brlen: %4.2f\n", i, tree->nodes[overlapNode1Node3[i]].brlens);		 
     	     }
	     if (tree->root != i && containsValue(overlapNode1Node4, i, totalNodes) && keepAdding12 == 1 && keepAdding == 1) {		     
                 brlen += tree->nodes[i].brlens;
 		 //printf("calcBrLen2 overlapNode1Node4[i]: %d brlen: %4.2f\n", i, tree->nodes[overlapNode1Node4[i]].brlens);		 
	     }



	}
	//printf("final brLen: %4.2f\n", brlen);
	return brlen; 
}
int containsValue(int *list, int value, int size) {
    int i = 0;
    for (i = 0; i < size; i++) {
        if (list[i] == value) {
		return 1;
	}
    }
    return 0;
}
void mergeOffsprings(int *offsprings1, int *offsprings2, int *combinedoffsprings, Tree *tree) {
	int i, j, index = 0;;
	// iterating through the internal nodes...
	int totalNodes = 2 * tree -> ntaxa - 1;
	for(i = 0; i < totalNodes; i++) { 	
	     if (offsprings1[i] != 0) {
	     	 for (j = 0; j < totalNodes; j++) {
	             if (offsprings1[i] == offsprings2[j]) {
                         combinedoffsprings[index] = offsprings1[i];
			 //printf("merge offsprings: %d\n", combinedoffsprings[index]);
			 index++;

		     }
	         }
	     }
	}    
}
void labelAllFather(int *offsprings, Tree *tree, int inode, int index) {
    offsprings[index] = inode;
    if (tree->root != inode) {

	int fathernode = tree->nodes[inode].father;
	labelAllFather(offsprings, tree, fathernode, index + 1);
    } else {

    }
}
void findOffsprings (int *offsprings, Tree *tree, int inode)
{
	int son0, son1;

	if(inode < tree->ntaxa)
		offsprings[inode] = 1; // if offspring consist of an leaf node.
	else
	{
		son0 = tree->nodes[inode].sons[0];
		son1 = tree->nodes[inode].sons[1];
		findOffsprings (offsprings, tree, son0);
		findOffsprings (offsprings, tree, son1);
	}
}

void findInternalOffsprings (int *offsprings, Tree *tree, int inode)
{
	int son0, son1;
	if(inode < tree->ntaxa)
		offsprings[inode] = 0; // if offspring consist of an leaf node.
	else
	{
		son0 = tree->nodes[inode].sons[0];
		son1 = tree->nodes[inode].sons[1];
		offsprings[son0] = 1;
		offsprings[son1] = 1;

		findInternalOffsprings (offsprings, tree, son0);
		findInternalOffsprings (offsprings, tree, son1);
	}
}

/**
 * Looks up the index numbering based on the quartet index lookup table
 * Possibly revise algorithm for faster lookup
 * By Tim Shaw*/
int findQuartet(int n1, int n2, int n3, int n4, long int *location) 
{
	int i; //, node1, node2, node3, node4;
	if ((n1 < n3 && n1 < n4 && n2 < n3 && n2 < n4) || (n3 < n1 && n3 < n2 && n4 < n1 && n4 < n2))
	{
		location[1] = 0;
	} else if ((n1 < n2 && n1 < n4 && n3 < n2 && n3 < n4) || (n2 < n1 && n2 < n3 && n4 < n1 && n4 < n3))
	{
		location[1] = 1;
	} else if ((n1 < n2 && n1 < n3 && n4 < n2 && n4 < n3) || (n2 < n1 && n2 < n4 && n3 < n1 && n3 < n4))
	{
		location[1] = 2;
	}

	// need a more efficient way of going through the quartetindex	
	for (i = 0; i < NChoose4Count; i++) {		
		if (quartetindex[i][n1] == 1 && quartetindex[i][n2] == 1 && quartetindex[i][n3] == 1 && quartetindex[i][n4] == 1) {
			location[0] = i;
			return(1);
		}		
	}
	return(1);
}

int findTriple (int n1, int n2, int n3, long int *location)
{
	int i, number=0, node1, node2, node3;

	if(n1 < n2 && n2 < n3)
	{
		node1 = n1;
		node2 = n2;
		node3 = n3;
		location[1] = 0;
	}
	else if(n2 < n1 && n1 < n3)
	{
		node1 = n2;
		node2 = n1;
		node3 = n3;
		location[1] = 0;
	}
	else if(n1 < n3 && n3 < n2)
	{
		node1 = n1;
		node2 = n3;
		node3 = n2;
		location[1] = 1;
	}
	else if(n2 < n3 && n3 < n1)
	{
		node1 = n2;
		node2 = n3;
		node3 = n1;
		location[1] = 1;
	}
	else if(n3 < n1 && n1 < n2)
	{
		node1 = n3;
		node2 = n1;
		node3 = n2;
		location[1] = 2;
	}
	else if(n3 < n2 && n2 < n1)
	{
		node1 = n3;
		node2 = n2;
		node3 = n1;
		location[1] = 2;
	}

	for(i=1; i<=node1; i++)
	{
		number += (sptree.ntaxa-i)*(sptree.ntaxa-i-1)/2;
	}
	for(i=node1+2; i<=node2; i++)
	{
		number += (sptree.ntaxa-i);
	}
	number += (node3 - node2 -1);
	location[0] = number;
	return(1);
}

int findNameNumber (Tree *tree)
{
	int i, j, k, stop;

	for(i=0; i<nGene; i++)
	{
		for(j=0; j<tree[i].ntaxa; j++)
		{
			stop = 0;
			for(k=0; k<totaltaxa; k++)
			{
				if(!strcmp(tree[i].nodes[j].taxaname, taxanames[k]))
				{
					stop = 1; 
					tree[i].nodes[j].namenumber = taxanodenumber[k];
					//printf("TaxaName:%s-%d\n",taxanames[k], taxanodenumber[k]); S1 = 0 S2 = 1 etc...
					break;
				}
			}
			if(stop == 0)
			{
				printf("missing taxa in gene%d\n",i);
				return (ERROR);
			}
		}
	}

	/*allocate memory for triplematrix*/
	triplematrix = (int**)calloc(sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)/6, sizeof(int*));
        triplematrix[0] = (int*)calloc(4*sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)/6, sizeof(int));
   	for(i = 0; i < (sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)/6); i++)
   		triplematrix[i] = triplematrix[0] + i*4;
  	if(!triplematrix)
	{
		printf(" allocating problem for triplematrix\n");
	   	return(ERROR);
	} 

	
	quartetmatrix = (int**)calloc(sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)*(sptree.ntaxa-3)/24, sizeof(int*));
	quartetmatrix[0] = (int*)calloc(4*sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)*(sptree.ntaxa-3)/24, sizeof(int));		
   	for(i = 0; i < sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)*(sptree.ntaxa-3)/24; i++)
   		quartetmatrix[i] = quartetmatrix[0] + i*4;
  	if(!quartetmatrix)
	{
		printf(" allocating problem for quartetmatrix\n");
	   	return(ERROR);
	} 


	quartetmatrixOnce = (int**)calloc(sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)*(sptree.ntaxa-3)/24, sizeof(int*));
	quartetmatrixOnce[0] = (int*)calloc(4*sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)*(sptree.ntaxa-3)/24, sizeof(int));		
   	for(i = 0; i < sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)*(sptree.ntaxa-3)/24; i++)
   		quartetmatrixOnce[i] = quartetmatrixOnce[0] + i*4;
  	if(!quartetmatrixOnce)
	{
		printf(" allocating problem for quartetmatrixonce\n");
	   	return(ERROR);
	} 

	

	int N = sptree.ntaxa;
	int M = 4;

	int px = 0;
	quartetindex = (int**)calloc(N * (N - 1)*(N - 2)*(N - 3) / 24, sizeof(int*));
	quartetindex[0] = (int*)calloc(N*N * (N - 1)*(N - 2)*(N - 3) / 24, sizeof(int));
   	for(i = 0; i < N *(N - 1)*(N - 2)*(N - 3) / 24; i++) {
   		quartetindex[i] = quartetindex[0] + i*N;
		for(px = 0; px < N; px++) { 
			quartetindex[i][px] = 0;
		}
	}


	NChoose4Count = N * (N - 1)*(N - 2)*(N - 3) / 24;



	int x, y, z, p[N+2], b[N], index = 0, n = 0;
	inittwiddle(M, N, p);
	for(i = 0; i != N-M; i++)
    	{
		b[i] = 0;
		//putchar('0');
	}
	while(i != N)
	{
		b[i++] = 1;
		//putchar('1');
	}
	//putchar('\n');
	index = 0;

	for(i = 0; i < N; i++) {
		quartetindex[index][i] = b[i];	    
	}
	while(!twiddle(&x, &y, &z, p))
	{
		index++;
		b[x] = 1;
		b[y] = 0;

		for(i = 0; i < N; i++) {
			quartetindex[index][i] = b[i];	    
		}
		n = 0;
	}

         
	if(!quartetindex)
	{
		printf(" allocating problem for triplematrix\n");
	   	return(ERROR);
	}
	return(NO_ERROR);
		
}

void MrBayesPrint (char *format, ...)
{
	va_list                 ptr;

	va_start (ptr, format);

			vprintf (format, ptr);
			fflush (stdout);

	va_end(ptr);

}

int findNgenesandNtaxa(FILE *fTree)
{
	char ch;
	int ncomma, ngene=0;

	ch = fgetc (fTree);
	while (ch != EOF) 
	{
      		ch = fgetc (fTree);
                //printf("%c", ch);
		ncomma = 0;
		while (ch != ';' && ch != EOF)
		{
			ch = fgetc (fTree);
                        //printf("%c", ch);
			if(ch == ',')
				ncomma++;
		}
      		if(ch == ';')
		{
			gtree[ngene].ntaxa = ncomma + 1;
        		ngene++;
		}
	}
	rewind(fTree);
        //printf("%d\n", ngene);        
	return (ngene);
}

int ReadaTree (FILE *fTree,Tree *tree)
{
/* 
   Both names and numbers for species are accepted.  
   Species names are considered case-sensitive, with trailing blanks ignored.
*/
   	int cnode, cfather=-1, taxa = 0;  /* current node and father */
   	int inodeb=0;  /* node number that will have the next branch length */
   	int i, level=0, ch=' ';
   	char  skips[]="\"\'";
   	int nnode;   

	nnode = tree->ntaxa; 
   	FOR(i,2*(tree->ntaxa)-1) {
      		tree->nodes[i].father=-1;
      		tree->nodes[i].nson=0; 
		tree->nodes[i].sons[0] = -1;
		tree->nodes[i].sons[1] = -1;
   	}

   	while(ch != '(')
     	{
      		ch=fgetc(fTree);
     	}
   	ungetc(ch,fTree);

   	for (;;) {
      		ch = fgetc (fTree);
      		if (ch==EOF) return(-1);
      		else if (!isgraph(ch) || ch==skips[0] || ch==skips[1]) continue;
      		else if (ch=='(') {
         		level++;
         		cnode=nnode++;
   
         		if(nnode > 2*(tree->ntaxa)-1)
              		{
                  		printf("check tree: perhaps too many '('s");
                  		exit(-1);
              		}
         		if (cfather>=0) {
            			tree->nodes[cfather].sons[tree->nodes[cfather].nson++] = cnode;
            			tree->nodes[cnode].father=cfather;
         		}
         		else
            			tree->root=cnode;
         		cfather=cnode;
      		}
      		else if (ch==')') { level--;  inodeb=cfather; cfather=tree->nodes[cfather].father; }
      		else if (ch==':') fscanf(fTree,"%lf",&tree->nodes[inodeb].brlens);
      		else if (ch==',') ;
      		else if (ch==';' && level!=0) 
         	{
            		printf("; in treefile");
            		exit(-1);
         	}
      		else if (isdigit(ch))
      		{ 
         		ungetc(ch, fTree); 
         		fscanf(fTree,"%d",&inodeb); 
         		inodeb--;
         		tree->nodes[inodeb].father=cfather;
         		tree->nodes[cfather].sons[tree->nodes[cfather].nson++]=inodeb;
      		}
		else if (isalpha(ch))
		{		
			i = 0;
			while(ch != ':' && ch != ',' && ch != ')')
			{
				if(ch != ' ')
					tree->nodes[taxa].taxaname[i++] = ch;
				ch = fgetc(fTree);
			}
			ungetc(ch, fTree);
			tree->nodes[taxa].father = cfather;
			tree->nodes[cfather].sons[tree->nodes[cfather].nson++] = taxa;
			inodeb = taxa;
			taxa++;
		}
      		if (level<=0) break;
   	}
   
   	for ( ; ; ) {
      		while(isspace(ch=fgetc(fTree)) && ch!=';' );
      		if (ch==':')       fscanf(fTree, "%lf", &tree->nodes[tree->root].brlens);
      		else if (ch==';')  break;
      		else  { ungetc(ch,fTree);  break; }
   	}

   	if(nnode != 2*(tree->ntaxa)-1) { printf(" # of nodes != %d\n",2*tree->ntaxa-1); exit(-1);}

   /*
	tree->nodes[tree->root].father = nTaxa-2;
	tree->nodes[2*nTaxa-2].sons[0] = tree->root;
	tree->nodes[tree->root].brlens = tree->nodes[Outgroup].brlens/2;

	tree->nodes[Outgroup].father = nTaxa-2;
	tree->nodes[2*nTaxa-2].sons[1] = Outgroup;
	tree->nodes[Outgroup].brlens += tree->nodes[Outgroup].brlens/2;

	tree->root = nTaxa-2;
	tree->nodes[tree->root].father = -1;
	tree->nodes[tree->root].nson = 2;
 	tree->nodes[tree->root].age = tree->nodes[Outgroup].brlens;*/

   return (0);
}

int PrintTree(Tree *tree, int inode, int showBrlens, int showTheta, int isRooted)
{

	char			*tempStr;
	int                     tempStrSize;

	/* allocate the print string */
	printStringSize = 200;
	printString = (char *)SafeMalloc((size_t) (printStringSize * sizeof(char)));
	if (!printString)
		{
		MrBayesPrint ("%s   Problem allocating printString (%d)\n", spacer, printStringSize * sizeof(char));
		return (ERROR);
		}
	*printString = '\0';

	tempStrSize = 200;
	tempStr = (char *) SafeMalloc((size_t) (tempStrSize * sizeof(char)));
	if (!tempStr)
		{
		MrBayesPrint ("%s   Problem allocating tempString (%d)\n", spacer, tempStrSize * sizeof(char));
		return (ERROR);
		}

	SaveSprintf (&tempStr, &tempStrSize,"(");
	AddToPrintString (tempStr);
					
	WriteTreeToFile (tree, tree->root, showBrlens, showTheta, isRooted);

	if(showTheta == YES) 
		SaveSprintf (&tempStr, &tempStrSize,")[#%lf];\n",tree->nodes[tree->root].theta);
	else 
		SaveSprintf (&tempStr, &tempStrSize,");\n");
	AddToPrintString (tempStr);
	free (tempStr); 

	return (NO_ERROR);					

}

int PrintPhylipTree(Tree *tree, int inode, int showBrlens, int showTheta, int isRooted)
{
	
	char			*tempStr;
	int                     tempStrSize;
	
	/* allocate the print string */
	printStringSize = 200;
	printString = (char *)SafeMalloc((size_t) (printStringSize * sizeof(char)));
	if (!printString)
	{
		MrBayesPrint ("%s   Problem allocating printString (%d)\n", spacer, printStringSize * sizeof(char));
		return (ERROR);
	}
	*printString = '\0';
	
	tempStrSize = 200;
	tempStr = (char *) SafeMalloc((size_t) (tempStrSize * sizeof(char)));
	if (!tempStr)
	{
		MrBayesPrint ("%s   Problem allocating tempString (%d)\n", spacer, tempStrSize * sizeof(char));
		return (ERROR);
	}
	
	SaveSprintf (&tempStr, &tempStrSize,"(");
	AddToPrintString (tempStr);
	
	WritePhylipTreeToFile (tree, tree->root, showBrlens, showTheta, isRooted);
	
	if(showTheta == YES) 
		SaveSprintf (&tempStr, &tempStrSize,")[#%lf];\n",tree->nodes[tree->root].theta);
	else 
		SaveSprintf (&tempStr, &tempStrSize,");\n");
	AddToPrintString (tempStr);
	free (tempStr); 
	
	return (NO_ERROR);					
	
}




void WriteTreeToFile (Tree *tree, int inode, int showBrlens, int showTheta, int isRooted)
{

		char			*tempStr;
		int                      tempStrSize = 200;

		tempStr = (char *) SafeMalloc((size_t) (tempStrSize * sizeof(char)));
		if (!tempStr)
			MrBayesPrint ("%s   Problem allocating tempString (%d)\n", spacer, tempStrSize * sizeof(char));


			
		if (tree->nodes[inode].nson == 0)
			{
				/*if (showBrlens == YES)
				{
    				SaveSprintf (&tempStr, &tempStrSize, "%d:%lf", inode+1, tree->nodes[inode].brlens);
					AddToPrintString (tempStr);
					if((tree->nodes[inode].theta>0) && showTheta == YES) 
					{
						SaveSprintf (&tempStr, &tempStrSize, "[#%lf]", tree->nodes[inode].theta);
						AddToPrintString (tempStr);
					}
				}
				else
				{*/
					SaveSprintf (&tempStr, &tempStrSize, "%d:9.00", inode+1);
					AddToPrintString (tempStr);
				/*}*/
			}
		else
			{
				if (inode != tree->root)
				{
					SaveSprintf (&tempStr, &tempStrSize, "(");
					AddToPrintString (tempStr);
				}
				WriteTreeToFile (tree,tree->nodes[inode].sons[0],  showBrlens, showTheta, isRooted);
				SaveSprintf (&tempStr, &tempStrSize, ",");
				AddToPrintString (tempStr);
				WriteTreeToFile (tree,tree->nodes[inode].sons[1], showBrlens, showTheta, isRooted);	
				if (inode != tree->root)
				{
					if (tree->nodes[inode].father == tree->root && isRooted == NO)
					{
						if (showBrlens == YES)
						{
							SaveSprintf (&tempStr, &tempStrSize, ",%d:%lf", tree->nodes[inode].father + 1, tree->nodes[tree->nodes[inode].father].brlens);
							AddToPrintString (tempStr);
			
							if((tree->nodes[tree->nodes[inode].father].theta>0) && showTheta == YES) 
							{
								SaveSprintf (&tempStr, &tempStrSize, "[#%lf]", tree->nodes[tree->nodes[inode].father].theta);
								AddToPrintString (tempStr);
							}
						}
						else
						{
							SaveSprintf (&tempStr, &tempStrSize, ",%d", tree->nodes[inode].father + 1);
							AddToPrintString (tempStr);
						}
					}
				
					if (showBrlens == YES && isRooted == YES) /*tree->nodes[inode].father != tree->root)*/
					{
						SaveSprintf (&tempStr, &tempStrSize,"):%lf", tree->nodes[inode].brlens);
						AddToPrintString (tempStr);
						if((tree->nodes[inode].theta > 0) && showTheta == YES)
						{
							SaveSprintf (&tempStr, &tempStrSize, "[#%lf]", tree->nodes[inode].theta);
							AddToPrintString (tempStr);
						}
					}
					else
					{
						SaveSprintf (&tempStr, &tempStrSize, ")");
						AddToPrintString (tempStr);
					}					
				}
			}
	free (tempStr);
		
		
}

void WritePhylipTreeToFile (Tree *tree, int inode, int showBrlens, int showTheta, int isRooted)
{
	
	char			*tempStr;
	int                      tempStrSize = 200;
	
	tempStr = (char *) SafeMalloc((size_t) (tempStrSize * sizeof(char)));
	if (!tempStr)
		MrBayesPrint ("%s   Problem allocating tempString (%d)\n", spacer, tempStrSize * sizeof(char));
	
	
	
	if (tree->nodes[inode].nson == 0)
	{
		/*if (showBrlens == YES)
		 {
		 SaveSprintf (&tempStr, &tempStrSize, "%d:%lf", inode+1, tree->nodes[inode].brlens);
		 AddToPrintString (tempStr);
		 if((tree->nodes[inode].theta>0) && showTheta == YES) 
		 {
		 SaveSprintf (&tempStr, &tempStrSize, "[#%lf]", tree->nodes[inode].theta);
		 AddToPrintString (tempStr);
		 }
		 }
		 else
		 {*/
		SaveSprintf (&tempStr, &tempStrSize, "%s:9.00", tree->nodes[inode].taxaname);
		AddToPrintString (tempStr);
		/*}*/
	}
	else
	{
		if (inode != tree->root)
		{
			SaveSprintf (&tempStr, &tempStrSize, "(");
			AddToPrintString (tempStr);
		}
		WritePhylipTreeToFile (tree,tree->nodes[inode].sons[0],  showBrlens, showTheta, isRooted);
		SaveSprintf (&tempStr, &tempStrSize, ",");
		AddToPrintString (tempStr);
		WritePhylipTreeToFile (tree,tree->nodes[inode].sons[1], showBrlens, showTheta, isRooted);	
		if (inode != tree->root)
		{
			if (tree->nodes[inode].father == tree->root && isRooted == NO)
			{
				if (showBrlens == YES)
				{
					SaveSprintf (&tempStr, &tempStrSize, ",%s:%lf", tree->nodes[tree->nodes[inode].father].taxaname, tree->nodes[tree->nodes[inode].father].brlens);
					AddToPrintString (tempStr);
					
					if((tree->nodes[tree->nodes[inode].father].theta>0) && showTheta == YES) 
					{
						SaveSprintf (&tempStr, &tempStrSize, "[#%lf]", tree->nodes[tree->nodes[inode].father].theta);
						AddToPrintString (tempStr);
					}
				}
				else
				{
					SaveSprintf (&tempStr, &tempStrSize, ",%s", tree->nodes[tree->nodes[inode].father].taxaname);
					AddToPrintString (tempStr);
				}
			}
			
			if (showBrlens == YES && isRooted == YES) /*tree->nodes[inode].father != tree->root)*/
			{
				SaveSprintf (&tempStr, &tempStrSize,"):%lf", tree->nodes[inode].brlens);
				AddToPrintString (tempStr);
				if((tree->nodes[inode].theta > 0) && showTheta == YES)
				{
					SaveSprintf (&tempStr, &tempStrSize, "[#%lf]", tree->nodes[inode].theta);
					AddToPrintString (tempStr);
				}
			}
			else
			{
				SaveSprintf (&tempStr, &tempStrSize, ")");
				AddToPrintString (tempStr);
			}					
		}
	}
	free (tempStr);
	
	
}

double tripleDist (Tree *tree1, Tree *tree2)
{
	int **triplematrix1, **triplematrix2;
	int i, j, k;
	long ntriples = sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)/6;
	double dist = 0.0;

	/*allocate memory for triplematrix1*/
	triplematrix1 = (int**)calloc(sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)/6, sizeof(int*));
        triplematrix1[0] = (int*)calloc(4*sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)/6, sizeof(int));
   	for(i = 0; i < (sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)/6); i++)
   		triplematrix1[i] = triplematrix1[0] + i*4;
  	if(!triplematrix1)
	{
		printf(" allocating problem for triplematrix1\n");
	   	return(ERROR);
	} 

	/*allocate memory for triplematrix*/
	triplematrix2 = (int**)calloc(sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)/6, sizeof(int*));
        triplematrix2[0] = (int*)calloc(4*sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)/6, sizeof(int));
   	for(i = 0; i < (sptree.ntaxa*(sptree.ntaxa-1)*(sptree.ntaxa-2)/6); i++)
   		triplematrix2[i] = triplematrix2[0] + i*4;
  	if(!triplematrix2)
	{
		printf(" allocating problem for triplematrix2\n");
	   	return(ERROR);
	} 

	if(triplesInatree (triplematrix1, tree1) == ERROR)
	{
		printf("Errors in the tripleInatree function 1\n");
		return (ERROR);
	}

	if(triplesInatree (triplematrix2, tree2) == ERROR)
	{
		printf("Errors in the tripleInatree function 2\n");
		return (ERROR);
	}

	for(j=0;j<ntriples;j++)
	{
		/*printf("triple %d %d %d\n",triplematrix1[j][0],triplematrix1[j][1],triplematrix1[j][2]);
		printf("triple %d %d %d\n",triplematrix2[j][0],triplematrix2[j][1],triplematrix2[j][2]);
		printf("\n");*/
		
		for(k=0; k<3; k++)
	 		dist += (triplematrix1[j][k] - triplematrix2[j][k]) * (triplematrix1[j][k] - triplematrix2[j][k]);
		
	}



	return(dist);


}


int AddToPrintString (char *tempStr)

{

        size_t                  len1, len2;

        len1 = (int) strlen(printString);
        len2 = (int) strlen(tempStr);
        if (len1 + len2 + 5 > printStringSize)
                {
                printStringSize += len1 + len2 - printStringSize + 200;
                printString = realloc((void *)printString, printStringSize * sizeof(char));
                if (!printString)
                        {
                        MrBayesPrint ("%s   Problem reallocating printString (%d)\n", spacer, printStringSize * sizeof(char));
                        goto errorExit;
                        }
                }
        strcat(printString, tempStr);
#       if 0
        printf ("printString(%d) -> \"%s\"\n", printStringSize, printString);
#       endif
        return (NO_ERROR);

        errorExit:
                return (ERROR);

}



#define TARGETLENDELTA (100)

int SaveSprintf(char **target, int *targetLen, char *fmt, ...) {
  va_list    argp;
  int        len,retval;

  va_start(argp, fmt);
#ifdef VISUAL
  len = _vsnprintf(NULL, 0, fmt, argp);
#else
  len = vsnprintf(NULL, 0, fmt, argp);
#endif

  va_end(argp);

  if(len>*targetLen)
        {
/*        fprintf(stderr, "* increasing buffer from %d to %d bytes\n", *targetLen, len+TARGETLENDELTA); */
        *targetLen = len+TARGETLENDELTA; /* make it a little bigger */
            *target = realloc(*target, *targetLen);
        }

  va_start(argp,fmt);
  retval=vsprintf(*target, fmt, argp);
  va_end(argp);

/*   fprintf(stderr, "* savesprintf /%s/\n",*target); */
  return retval;
}

void *SafeMalloc(size_t s) {
        void *ptr = malloc(s);
        if(ptr==NULL)
                return NULL;
        return memset(ptr,0,s);
}


/**
 * The following is a slight modification of twiddle.c 
 * twiddle generate all combinations of M elements drawn without replacement
  from a set of N elements.  This routine may be used in two ways:
  (0) To generate all combinations of M out of N objects, let a[0..N-1]
      contain the objects, and let c[0..M-1] initially be the combination
      a[N-M..N-1].  While twiddle(&x, &y, &z, p) is false, set c[z] = a[x] to
      produce a new combination.
  (1) To generate all sequences of 0's and 1's containing M 1's, let
      b[0..N-M-1] = 0 and b[N-M..N-1] = 1.  While twiddle(&x, &y, &z, p) is
      false, set b[x] = 1 and b[y] = 0 to produce a new sequence.
  In either of these cases, the array p[0..N+1] should be initialised as
  follows:
    p[0] = N+1
    p[1..N-M] = 0
    p[N-M+1..N] = 1..M
    p[N+1] = -2
    if M=0 then p[1] = 1
  In this implementation, this initialisation is accomplished by calling
  inittwiddle(M, N, p), where p points to an array of N+2 ints.

  Coded by Matthew Belmonte <mkb4@Cornell.edu>, 23 March 1996.  This
  implementation Copyright (c) 1996 by Matthew Belmonte.  Permission for use and
  distribution is hereby granted, subject to the restrictions that this
  copyright notice and reference list be included in its entirety, and that any
  and all changes made to the program be clearly noted in the program text.

  This software is provided 'as is', with no warranty, express or implied,
  including but not limited to warranties of merchantability or fitness for a
  particular purpose.  The user of this software assumes liability for any and
  all damages, whether direct or consequential, arising from its use.  The
  author of this implementation will not be liable for any such damages.

  Reference:

  Phillip J Chase, `Algorithm 382: Combinations of M out of N Objects [G6]',
  Communications of the Association for Computing Machinery 13:6:368 (1970).

  The returned indices x, y, and z in this implementation are decremented by 1,
  in order to conform to the C language array reference convention.  Also, the
  parameter 'done' has been replaced with a Boolean return value.
 * 
 */
int twiddle(x, y, z, p)
int *x, *y, *z, *p;
  {
  register int i, j, k;
  j = 1;
  while(p[j] <= 0)
    j++;
  if(p[j-1] == 0)
    {
    for(i = j-1; i != 1; i--)
      p[i] = -1;
    p[j] = 0;
    *x = *z = 0;
    p[1] = 1;
    *y = j-1;
    }
  else
    {
    if(j > 1)
      p[j-1] = 0;
    do
      j++;
    while(p[j] > 0);
    k = j-1;
    i = j;
    while(p[i] == 0)
      p[i++] = -1;
    if(p[i] == -1)
      {
      p[i] = p[k];
      *z = p[k]-1;
      *x = i-1;
      *y = k-1;
      p[k] = -1;
      }
    else
      {
      if(i == p[0])
        return(1);
      else
        {
        p[j] = p[i];
        *z = p[i]-1;
        p[i] = 0;
        *x = j-1;
        *y = i-1;
        }
      }
    }
  return(0);
}

void inittwiddle(m, n, p)
int m, n, *p;
  {
  int i;
  p[0] = n+1;
  for(i = 1; i != n-m+1; i++)
    p[i] = 0;
  while(i != n+1)
    {
    p[i] = i+m-n;
    i++;
    }
  p[n+1] = -2;
  if(m == 0)
    p[1] = 1;
  }

