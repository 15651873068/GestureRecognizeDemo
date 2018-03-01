//
// File: my_filter.cpp
//
// MATLAB Coder version            : 3.2
// C/C++ source code generated on  : 01-Mar-2018 13:01:10
//

// Include Files
#include "rt_nonfinite.h"
#include "my_filter.h"

// Function Definitions

//
// Arguments    : const double hn[197]
//                double a
//                const double x[4400]
//                double y[4400]
// Return Type  : void
//
void my_filter(const double hn[197], double a, const double x[4400], double y
               [4400])
{
  double b[197];
  int k;
  int j;
  memcpy(&b[0], &hn[0], 197U * sizeof(double));
  if ((!rtIsInf(a)) && (!rtIsNaN(a)) && (!(a == 0.0)) && (a != 1.0)) {
    for (k = 0; k < 197; k++) {
      b[k] /= a;
    }
  }

  memset(&y[0], 0, 4400U * sizeof(double));
  for (k = 0; k < 197; k++) {
    for (j = k; j + 1 < 4401; j++) {
      y[j] += b[k] * x[j - k];
    }
  }
}

//
// Arguments    : void
// Return Type  : void
//
void my_filter_initialize()
{
  rt_InitInfAndNaN(8U);
}

//
// Arguments    : void
// Return Type  : void
//
void my_filter_terminate()
{
  // (no terminate code required)
}

//
// File trailer for my_filter.cpp
//
// [EOF]
//
