import os
import ast
import SourceTree
import argparse

parser = argparse.ArgumentParser(description='Search Pattern in directory ')
parser.add_argument('pattern_dir', metavar='Pattern Diretory', type=str, help='pattern dir for analise')
parser.add_argument('source_dir', metavar='Source Diretory', type=str, help='source dir for analise')
parser.add_argument('output', metavar='output', type=str, help='file output')

args = parser.parse_args()

outputfile = args.output

f = open(outputfile, "w")


def get_pyfile_in_diretory(base_path):
    all_path_source_file = list()
    for path, dirs, files in os.walk(base_path):
        for filename in files:
            if filename.endswith(".py"):
                fullpath = os.path.join(path, filename)
                all_path_source_file.append(fullpath)
    return all_path_source_file


def write_occurrence_in_file(occurrences, pattern, source):
    for oc in occurrences:
        for i in oc:
            ini_lineno = i.get("initial_position").get("lineno")
            ini_col = i.get("initial_position").get("col_offset")
            
            end_lineno = i.get("end_position").get("lineno")
            end_col = i.get("end_position").get("col_offset")            

            initial_position = '(l-{0}, c-{1})'.format(ini_lineno, ini_col)
            end_position = '(l-{0}, c-{1})'.format(end_lineno, end_col)

            print(pattern, source, initial_position, end_position)

            line = '[ PATTERN: {0}] - [ SOURCE: {1} ] \n \n --Initial Position: {2} | \n --End Position: {3}\n\n'.format(pattern, source, initial_position, end_position )
            f.write(line + "\n")


           



def search_pattern_in_source(pattern_files, source_files):
    for s_path_file in source_files:
        with open(s_path_file, 'r') as s_file:
            try:
                source_code = ast.parse(s_file.read())
                tree = SourceTree.SourceTree(source_code)
            except:
                print("Erro na leitura do arquivo:", s_path_file)
                continue;
            for p_path_file in pattern_files:
                with open(p_path_file, 'r') as p_file:
                    source_pattern = ast.parse(p_file.read())
                    response = tree.get_positions_pattern(source_pattern)
                    write_occurrence_in_file(response, p_path_file,s_path_file)
                   



pattern_files = get_pyfile_in_diretory(args.pattern_dir)
source_files = get_pyfile_in_diretory(args.source_dir)

print(args)
print(pattern_files)
print("------------")
print(source_files)

search_pattern_in_source(pattern_files, source_files)
