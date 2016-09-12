#!/usr/bin/python
# Python script for tree comparison
# Usage:
# python treeComp.py speciestree genetree
#
# The output of the script will be a set of images
# that labels the different bifurcations in gene 
# trees.
#
# Edited by Zheng Ruan(zruan1991@gmail.com)
# Last updated on Mar 26, 2013
#
import matplotlib
#matplotlib.use('Agg')
#import matplotlib.pyplot as plt
#plt.plot([1,2,3])
#plt.savefig('myfig')
import sys
import copy
from Bio import Phylo

def main(args):

    
    def get_internal2tips(tree):
        """ Function of getting internal nodes tip nodes correspondance
        """
        internal2tips = {}
        internalNodes = tree.get_nonterminals()
        c = 1
        for clade in internalNodes:
            clade.Name = 'C' + str(c)
            c = c + 1
            tips = clade.get_terminals()
            internal2tips[clade.Name] = [i.name for i in tips]
        return internal2tips

    def compareInter2tips(speciesInter2tips, geneInter2tips, genetree):
        """ Function of comparing each internal2tips dict for species
        tree and gene tree
        """
        for i in geneInter2tips:
            clade = genetree.common_ancestor(geneInter2tips[i])
            for j in speciesInter2tips:
                if set(geneInter2tips[i]) == set(speciesInter2tips[j]):
                    clade.color = 'gray'
                    break
                else:
                    clade.color = 'blue'
        return genetree

    speciestree = Phylo.read(args[1], 'newick') 
    speciestreeInternal2tips = get_internal2tips(speciestree)

    genetrees = Phylo.parse(args[2], 'newick')

    i = 1
    for tree in genetrees:
        if (i < 100):
	        geneInternal2tips = get_internal2tips(tree)
	
	        #Phylo.draw(tree)
	        # plot different bifurcations in gene tree
	        coloredGeneTree = copy.deepcopy(tree)
	        compareInter2tips(speciestreeInternal2tips, geneInternal2tips, coloredGeneTree)
	        coloredGeneTree.root.branch_length = 0
	        #Phylo.draw(coloredGeneTree)
	        fig1 = Phylo.draw(coloredGeneTree, do_show=False)
	        #fig1.savefig("test");                
	        fig1.savefig('diffGene' + str(i) + '.svg', transparent=True)
	
	        # plot different bifurcations in species tree
	        coloredSpeciesTree = copy.deepcopy(speciestree)
	        compareInter2tips(geneInternal2tips, speciestreeInternal2tips, coloredSpeciesTree)
	        coloredSpeciesTree.root.branch_length = 0
	        #Phylo.draw(coloredSpeciesTree)
	        fig2 = Phylo.draw(coloredSpeciesTree, do_show=False)
	        fig2.savefig('diffSpecies' + str(i) + '.svg', transparent=True)
        i = i + 1

if __name__ == '__main__':
    main(sys.argv)


