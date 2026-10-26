DOCS_DIR = docs
OUT_DIR = target

SPEC = spec

.PHONY: all
all: spec

.PHONY: spec
spec: $(DOCS_DIR)/$(SPEC)/codingwindow.svg $(DOCS_DIR)/$(SPEC)/saveload.svg
	pandoc --resource-path=$(DOCS_DIR)/$(SPEC) \
		-V lang=hu -V polyglossia-otherlangs="" \
		-M babel-lang=magyar \
		-t pdf --pdf-engine=xelatex \
		-s $(DOCS_DIR)/$(SPEC)/spec.md -o $(OUT_DIR)/$(SPEC).pdf

%.svg: %.puml
	plantuml -tsvg $<
# %.png: %.puml
# 	plantuml $< -tpng

# %.svg: %.dot
# 	dot -Tsvg $< -o $@

.PHONY: clean
clean:
	rm -f $(OUT_DIR)/$(SPEC).pdf
	rm -f $(DOCS_DIR)/$(SPEC)/*.svg
