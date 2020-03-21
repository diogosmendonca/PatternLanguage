import ast
from src.SourceTree2 import SourceTree

str_source_pattern = """

mundo = 10
mundo = 20
    

"""
str_source_code = """ 
try:
    mundo = 10
    mundo = 20
    mundo = 20
except:
    mundo = 10
    
"""

source_code = ast.parse(str_source_code)
source_pattern = ast.parse(str_source_pattern)

tree1 = SourceTree(source_code)
# notOp = tree1.get_not(str_source_pattern)
# print(tree1.notsPattern)
# print("Arvores Iguais? - ", tree1.is_equals(source_pattern))
# print("É Subarvore ? - ", tree1.is_subtree(source_pattern))

# print("Padrao encontrado:", tree1.brute_equals_two_nodes(source_code, source_pattern))
print("Padrao encontrado:", tree1.search_pattern(source_pattern))
# print(tree1.get_positions_pattern(source_pattern))


# print(tree1.amount_of_patterns_found(source_pattern))


# tree1.get_all_name_variable()

