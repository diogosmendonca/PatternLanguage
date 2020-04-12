import os
import sys
import ast
import SourceTree
from pprint import pprint

parameterlist = sys.argv
path = sys.argv[(parameterlist.index("-p") + 1)]
dirlist = os.listdir(path)


str_pattern = """
someVariable = 'any'
print(someVariable)
"""

source_pattern = ast.parse(str_pattern)
cont = 0
for path, dirs, files in os.walk(path):
    for filename in files:
        if filename.endswith(".py"):
            fullpath = os.path.join(path, filename)
            with open(fullpath, 'r') as f:
                source_code = ast.parse(f.read())
                tree = SourceTree.SourceTree(source_code)
                print( path + filename, tree.get_positions_pattern(source_pattern))
                cont +=1


print("Foram analisados ", cont , "arquivos")