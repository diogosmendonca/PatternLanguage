from oldcode import subtree
import ast

source = """
print(1)
"""
source2 = """   
print(var3)
"""

node = ast.parse(source)
node2 = ast.parse(source2)
analy = subtree.Analyzer(node, node2)
print("Ocorrencia(s): ")
analy.status()
