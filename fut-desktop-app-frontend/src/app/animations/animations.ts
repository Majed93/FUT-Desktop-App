import {animate, state, style, transition, trigger} from '@angular/animations';

// trigger name for attaching this animation to an element using the [@triggerName] syntax
export function sideBarAnimation() {
  return trigger('slideSideBar', [
    state('left', style({transform: 'translateX(0%)'})),
    state('right', style({transform: 'translateX(-80%)'})),
    transition('left => right', animate('0.5s ease-in-out')),
    transition('right => left', animate('0.5s ease-in-out')),
  ]);
}

