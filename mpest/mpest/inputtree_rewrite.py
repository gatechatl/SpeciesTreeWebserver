#!/usr/bin/env python
# Filename: inputtree_rewrite.py

from Bio import Phylo
import sys
from StringIO import StringIO

def main(args):
    filename = args[1]
    trees = Phylo.parse(filename, 'newick')
    handle = StringIO()
    Phylo.write(trees, handle, 'newick')
    print handle.getvalue();

if __name__ == '__main__':
    main(sys.argv)
