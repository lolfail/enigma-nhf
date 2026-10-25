DOCS_DIR = docs
OUT_DIR = target

SPEC = spec

.PHONY: all
all: spec

spec:
	pandoc --resource-path=$(DOCS_DIR)/$(SPEC) \
		-V lang=hu -V polyglossia-otherlangs="" \
		-V header-includes="\usepackage{graphicx}\usepackage{subcaption}\graphicspath{{$(DOCS_DIR)/$(SPEC)}}" \
		-M babel-lang=magyar \
		-t pdf --pdf-engine=xelatex \
		-s $(DOCS_DIR)/$(SPEC)/spec.md -o $(OUT_DIR)/$(SPEC).pdf

%.svg: %.puml
	plantuml $< -tsvg
%.svg: %.dot
	dot -Tsvg $< -o $@

.PHONY: clean
clean:
	rm $(OUT_DIR)/$(SPEC).pdf
