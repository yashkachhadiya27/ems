import { CanDeactivateFn } from '@angular/router';
import { RegisterComponent } from '../../pages/Common/register/register.component';

export const formLeaveGuard: CanDeactivateFn<RegisterComponent> = (
  component,
  currentRoute,
  currentState,
  nextState
) => {
  return component.canExit();
};
