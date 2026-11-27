DOCS_DIR = docs
OUT_DIR = target

.PHONY: all
all: spec prog user $(DOCS_DIR)/classisolated.svg $(DOCS_DIR)/classconnected.svg

SPEC = spec
.PHONY: spec
spec: $(DOCS_DIR)/$(SPEC)/codingwindow.svg $(DOCS_DIR)/$(SPEC)/saveload.svg $(DOCS_DIR)/$(SPEC)/rotorconfig.svg $(DOCS_DIR)/$(SPEC)/plugboardconfig.svg
	pandoc --resource-path=$(DOCS_DIR)/$(SPEC) \
		-V lang=hu -V polyglossia-otherlangs="" \
		-M babel-lang=magyar \
		-t pdf --pdf-engine=xelatex \
		-s $(DOCS_DIR)/$(SPEC)/$(SPEC).md -o $(OUT_DIR)/$(SPEC).pdf

PROG = prog
.PHONY: prog
prog: $(DOCS_DIR)/$(PROG)/enigmamodel.svg $(DOCS_DIR)/$(PROG)/utilsmodel.svg $(DOCS_DIR)/$(PROG)/graphicsmodel.svg $(DOCS_DIR)/$(PROG)/translationlayermodel.svg
	pandoc --resource-path=$(DOCS_DIR)/$(PROG) \
		--pdf-engine-opt=-shell-escape \
		-V lang=hu -V polyglossia-otherlangs="" \
		-M babel-lang=magyar \
		-t pdf --pdf-engine=xelatex \
		-s $(DOCS_DIR)/$(PROG)/$(PROG).md -o $(OUT_DIR)/$(PROG).pdf

USER = user
.PHONY: user
user:
	pandoc --resource-path=$(DOCS_DIR)/$(USER) \
		-V lang=hu -V polyglossia-otherlangs="" \
		-M babel-lang=magyar \
		-t pdf --pdf-engine=xelatex \
		-s $(DOCS_DIR)/$(USER)/$(USER).md -o $(OUT_DIR)/$(USER).pdf

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
	rm -f $(OUT_DIR)/$(PROG).pdf
	rm -f $(DOCS_DIR)/$(PROG)/*.svg
	rm -rf svg-inkscape
	rm -rf staging
	rm -f $(DOCS_DIR)/*.svg
	rm -f archive.zip

.PHONY: package
package: all
	mkdir staging

	mkdir staging/docs
	cp $(OUT_DIR)/$(PROG).pdf staging/docs/progdoc.pdf
	cp $(OUT_DIR)/$(USER).pdf staging/docs/userdoc.pdf
	cp $(OUT_DIR)/$(SPEC).pdf staging/docs/spec.pdf
	cp $(DOCS_DIR)/*.svg staging/docs/

	cp -r src/ staging/
	cp pom.xml staging
	cp README.md staging
	cp ukwb.xml staging

	(cd staging && zip -r ../archive.zip .)
