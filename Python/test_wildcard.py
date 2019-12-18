import subtree
import ast

source = """
def anyFunction(varA, varB):
    print(varA)
"""
source2 = """   
print(var3)
"""

node = ast.parse(source)
node2 = ast.parse(source2)
analy = subtree.Analyzer(node, node2)
print("Ocorrencia(s): ")
analy.status()
