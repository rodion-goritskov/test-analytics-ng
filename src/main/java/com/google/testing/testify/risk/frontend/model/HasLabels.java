package com.google.testing.testify.risk.frontend.model;

import java.util.List;

/**
 * Interface that defines some commonality between items that have labels.  For use in making
 * some functions generic.
 * 
 * @author jimr@google.com (Jim Reardon)
 */
public interface HasLabels {

  public Long getId();
  public long getParentProjectId();
  public void setAccLabels(List<AccLabel> labels);
  public void addLabel(AccLabel label);
  public List<AccLabel> getAccLabels();
  public AccElementType getElementType();

}
