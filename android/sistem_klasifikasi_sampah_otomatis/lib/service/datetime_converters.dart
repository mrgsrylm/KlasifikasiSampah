import 'package:intl/intl.dart';

class DatetimeConverters {
  static final DatetimeConverters constants = DatetimeConverters._();
  factory DatetimeConverters() => constants;
  DatetimeConverters._();
  String convertToAsiaJakarta(int unixTimestampMillis) {
    DateTime dateTimeMillis = DateTime.fromMillisecondsSinceEpoch(unixTimestampMillis);    
    String result = DateFormat.yMd().add_Hms().format(dateTimeMillis.toLocal());

    return result;
  }
}