import subtree
import ast

source = """aaaaaaaaaaaaaaaaa
Account.objects.get(id=id)
print(valor) 
valor = 10

"""
source2 = """
valor = 10
Account.objects.get(id=id)
    
"""

node = ast.parse(source)
node2 = ast.parse(source2)

print(subtree.Analyzer(node,node2).status)
