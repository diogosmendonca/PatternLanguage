import ast
from functools import partial
from nltk.tree import Tree

try:
    _basestring = basestring
except NameError:
    _basestring = str


def _strip_docstring(body):
    first = body[0]
    if isinstance(first, ast.Expr) and isinstance(first.value, ast.Str):
        return body[1:]
    return body


def handle_ast(node, omit_docstrings):
    return recurse_through_ast(
        node,
        partial(handle_ast, omit_docstrings=omit_docstrings),
        handle_terminal,
        handle_fields,
        handle_no_fields,
        omit_docstrings,
    )


def handle_terminal(terminal):
    return str(terminal)


def handle_fields(node, fields):
    return '({.__class__.__name__} {} )'.format(node, ' '.join(fields))


def handle_no_fields(node):
    return node.__class__.__name__


def recurse_through_ast(node, handle_ast, handle_terminal, handle_fields, handle_no_fields, omit_docstrings):
    possible_docstring = isinstance(node, (ast.FunctionDef, ast.ClassDef, ast.Module))

    node_fields = zip(
        node._fields,
        (getattr(node, attr) for attr in node._fields)
    )
    field_results = []
    for field_name, field_value in node_fields:
        if isinstance(field_value, ast.AST):
            field_results.append(handle_ast(field_value))

        elif isinstance(field_value, list):
            if possible_docstring and omit_docstrings and field_name == 'body':
                field_value = _strip_docstring(field_value)
            field_results.extend(
                handle_ast(item)
                if isinstance(item, ast.AST) else
                handle_terminal(item)
                for item in field_value
            )

        elif isinstance(field_value, _basestring):
            field_results.append(handle_terminal('"{}"'.format(field_value)))

        elif field_value is not None:
            field_results.append(handle_terminal(field_value))

    if not field_results:
        return handle_no_fields(node)


    return handle_fields(node, field_results)



source = "Account.objects.get(id=id)"
node = ast.parse(source)
str_tree = handle_ast(node, True)
t = Tree.fromstring(str_tree)
t.draw()
print(t.flatten())
