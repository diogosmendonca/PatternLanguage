import ast
import io
from src.SourceTree import SourceTree

str_source_pattern = """

"""
str_source_code = """

"""

source_code = ast.parse(str_source_code)
source_pattern = ast.parse(str_source_pattern)

tree1 = SourceTree(source_code)

# print("Arvores Iguais? - ", tree1.is_equals(source_pattern))
# print("É Subarvore ? - ", tree1.is_subtree(source_pattern))

print("Contem o padrao no Codigo ?", tree1.search_pattern(source_pattern))
print("Contem o padrao no Codigo ?", tree1.get_positions_pattern(str_source_pattern))


# print(tree1.amount_of_patterns_found(source_pattern))


# tree1.get_all_name_variable()

