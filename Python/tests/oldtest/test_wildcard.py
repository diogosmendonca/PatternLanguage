from oldcode import subtree
import ast

source = """
funcao(a)
"""
source2 = """   
funcao(a)
"""

node = ast.parse(source)
node2 = ast.parse(source2)
analy = subtree.Analyzer(node, node2)

print("Ocorrencia(s): ")
analy.status()

