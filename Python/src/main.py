import ast
import SourceTree

source = """
print(anyVariaveis)
anyVariavel = 10
"""
source2 = """ 
def olaMundo():
    if(a == 10):
        try:
            while(True):
                print(anyVariaveis)
                anyVariavel = 10
        except:
            print("Ola")
"""

node = ast.parse(source)
node2 = ast.parse(source2)

tree1 = SourceTree.SourceTree(node)

print (tree1.is_equals(node2))
print (tree1.is_subtree(node2))





