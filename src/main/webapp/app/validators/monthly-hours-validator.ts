import { AbstractControl } from '@angular/forms';

export function monthlyHoursValidator(
    control: AbstractControl
): {
    [key: string]: any;
} | null {
    const MAX_MONTHLY_HOURS = 744;
    const valid = control.value >= 0 && control.value <= MAX_MONTHLY_HOURS;
    return valid ? null : { invalidNumber: true };
}
