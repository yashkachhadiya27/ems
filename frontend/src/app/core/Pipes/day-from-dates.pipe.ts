import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'dayFromDates',
  standalone: true,
})
export class DayFromDatesPipe implements PipeTransform {
  transform(startDate: string | Date, endDate: string | Date): number {
    if (!startDate || !endDate) {
      return 0;
    }
    const start = new Date(startDate);
    const end = new Date(endDate);
    const timeDiff = Math.abs(end.getTime() - start.getTime());
    const diffDays = Math.ceil(timeDiff / (1000 * 3600 * 24)) + 1;
    return diffDays;
  }
}
