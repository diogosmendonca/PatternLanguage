import ast
from src.SourceTree import SourceTree

str_source_pattern = """
def funcao():
    #start-not
    
    print(20)
    
    #end-not

"""
str_source_code = """ 
def funcao():    
    a = 50
    
    
"""

source_code = ast.parse(str_source_code)
source_pattern = ast.parse(str_source_pattern)

tree1 = SourceTree(source_code)
notOp = tree1.get_not(str_source_pattern)
# print(tree1.notsPattern)
# print("Arvores Iguais? - ", tree1.is_equals(source_pattern))
# print("É Subarvore ? - ", tree1.is_subtree(source_pattern))

tree1.search_pattern(str_source_pattern)
# tree1.get_positions_pattern(source_pattern)


# print(tree1.amount_of_patterns_found(source_pattern))


# tree1.get_all_name_variable()

