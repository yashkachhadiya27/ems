import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'age',
  standalone: true,
})
export class AgePipe implements PipeTransform {
  transform(dob: string | Date): number {
    let dateOfBirth = new Date(dob);
    let today = new Date();
    let age = today.getFullYear() - dateOfBirth.getFullYear();
    let monthDifference = today.getMonth() - dateOfBirth.getMonth();
    let dayDifference = today.getDay() - dateOfBirth.getDay();
    if (monthDifference < 0 || (monthDifference === 0 && dayDifference < 0)) {
      age--;
    }
    return age;
  }
}
