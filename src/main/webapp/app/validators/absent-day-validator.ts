import { AbstractControl } from '@angular/forms';

export function absentDaysValidator(
    control: AbstractControl
): {
    [key: string]: any;
} | null {
    const MAX_ABSENT_DAYS = 23;
    const valid = control.value >= 0 && control.value <= MAX_ABSENT_DAYS;
    return valid ? null : { invalidNumber: true };
}
