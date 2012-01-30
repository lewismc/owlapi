/*
 * This file is part of the OWL API.
 *
 * The contents of this file are subject to the LGPL License, Version 3.0.
 *
 * Copyright (C) 2011, The University of Manchester
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0
 * in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 *
 * Copyright 2011, University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.manchester.cs.owl.owlapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.CollectionFactory;


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Bio-Health Informatics Group<br> Date:
 * 26-Oct-2006<br><br>
 */
public class OWLEquivalentClassesAxiomImpl extends OWLNaryClassAxiomImpl implements OWLEquivalentClassesAxiom {


	private static final long serialVersionUID = -7012904259532092297L;
	private Set<OWLClass> namedClasses;
    @SuppressWarnings("javadoc")
    public OWLEquivalentClassesAxiomImpl(OWLDataFactory dataFactory, Set<? extends OWLClassExpression> classExpressions, Collection<? extends OWLAnnotation> annotations) {
        super(dataFactory, classExpressions, annotations);
        namedClasses = null;
    }

    public OWLEquivalentClassesAxiom getAxiomWithoutAnnotations() {
        if (!isAnnotated()) {
            return this;
        }
        return getOWLDataFactory().getOWLEquivalentClassesAxiom(getClassExpressions());
    }

    public OWLEquivalentClassesAxiom getAnnotatedAxiom(Set<OWLAnnotation> annotations) {
        return getOWLDataFactory().getOWLEquivalentClassesAxiom(getClassExpressions(), mergeAnnos(annotations));
    }


    public Set<OWLEquivalentClassesAxiom> asPairwiseAxioms() {
        List<OWLClassExpression> classExpressions = new ArrayList<OWLClassExpression>(getClassExpressions());
        Set<OWLEquivalentClassesAxiom> result = new HashSet<OWLEquivalentClassesAxiom>();
        for (int i = 0; i < classExpressions.size() - 1; i++) {
            OWLClassExpression ceI = classExpressions.get(i);
            OWLClassExpression ceJ = classExpressions.get(i + 1);
            result.add(getOWLDataFactory().getOWLEquivalentClassesAxiom(ceI, ceJ));
        }
        return result;
    }

    public boolean containsNamedEquivalentClass() {
        return !getNamedClasses().isEmpty();
    }

    public boolean containsOWLNothing() {
        for (OWLClassExpression desc : getClassExpressions()) {
            if (desc.isOWLNothing()) {
                return true;
            }
        }
        return false;
    }


    public boolean containsOWLThing() {
        for (OWLClassExpression desc : getClassExpressions()) {
            if (desc.isOWLThing()) {
                return true;
            }
        }
        return false;
    }

    public Set<OWLClass> getNamedClasses() {
        if (namedClasses == null) {
            Set<OWLClass> clses = new HashSet<OWLClass>(1);
            for (OWLClassExpression desc : getClassExpressions()) {
                if (!desc.isAnonymous() && !desc.isOWLNothing() && !desc.isOWLThing()) {
                    clses.add(desc.asOWLClass());
                }
            }
            namedClasses = Collections.unmodifiableSet(clses);
        }
        return CollectionFactory.getCopyOnRequestSet(namedClasses);
    }

    public Set<OWLSubClassOfAxiom> asOWLSubClassOfAxioms() {
        Set<OWLSubClassOfAxiom> result = new HashSet<OWLSubClassOfAxiom>();
        for (OWLClassExpression descA : getClassExpressions()) {
            for (OWLClassExpression descB : getClassExpressions()) {
                if (!descA.equals(descB)) {
                    result.add(getOWLDataFactory().getOWLSubClassOfAxiom(descA, descB));
                }
            }
        }
        return result;
    }

    @Override
	public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof OWLEquivalentClassesAxiom;
    }


    public void accept(OWLAxiomVisitor visitor) {
        visitor.visit(this);
    }


    public void accept(OWLObjectVisitor visitor) {
        visitor.visit(this);
    }


    public <O> O accept(OWLAxiomVisitorEx<O> visitor) {
        return visitor.visit(this);
    }


    public <O> O accept(OWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }


    public AxiomType<?> getAxiomType() {
        return AxiomType.EQUIVALENT_CLASSES;
    }
}
