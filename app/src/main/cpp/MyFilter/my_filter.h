//
// File: my_filter.h
//
// MATLAB Coder version            : 3.2
// C/C++ source code generated on  : 01-Mar-2018 13:01:10
//
#ifndef MY_FILTER_H
#define MY_FILTER_H

// Include Files
#include <stddef.h>
#include <stdlib.h>
#include <string.h>
#include "rt_nonfinite.h"
#include "rtwtypes.h"
#include "my_filter_types.h"

// Function Declarations
extern void my_filter(const double hn[197], double a, const double x[4400],
                      double y[4400]);
extern void my_filter_initialize();
extern void my_filter_terminate();

#endif

//
// File trailer for my_filter.h
//
// [EOF]
//
